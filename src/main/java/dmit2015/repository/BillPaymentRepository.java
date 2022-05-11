package dmit2015.repository;

import dmit2015.entity.Bill;
import dmit2015.entity.BillPayment;

import dmit2015.security.BillPaymentSecurityInterceptor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.SecurityContext;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
@Interceptors(BillPaymentSecurityInterceptor.class)
public class BillPaymentRepository {

    @PersistenceContext
    private EntityManager _entityManager;

    @Inject
    private SecurityContext _securityContext;

    public Optional<BillPayment> findOneById(Long id) {
        Optional<BillPayment> optionalBillPayment = Optional.empty();
        try {
            BillPayment querySingleResult = _entityManager.find(BillPayment.class, id);
            if (querySingleResult != null) {
                optionalBillPayment = Optional.of(querySingleResult);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return optionalBillPayment;
    }

    /**
     * Return all BillPayment for the current user
     * @return list of BillPayment for the current user
     */
    public List<BillPayment> findAll() {
        List<BillPayment> payments = null;

        /* TODO: Modify code to work as follows:
            If the caller is anonymous (non-authenticated) user then throw an `RuntimeException`.
            If the caller is the role *Finance* then return a list of Bill entity filter by the username of the caller.
            If the caller is the role *Accounting* or *Executive* then return a list of a Bill entity.
            If the caller is not in the role *Finance* or *Accounting* or *Executive* then return a empty list.
         */

        if (_securityContext.getCallerPrincipal() == null ) {
            throw new RuntimeException("User not authenticated.");
        }

        if (_securityContext.isCallerInRole("Finance")) {
            String username = _securityContext.getCallerPrincipal().getName();
            payments = _entityManager.createQuery(
                            "SELECT b FROM BillPayment b WHERE b.authenticatedUsername = :usernameValue", BillPayment.class)
                    .setParameter("usernameValue", username).getResultList();
        }

        if (_securityContext.isCallerInRole("Accounting") || _securityContext.isCallerInRole("Executive")) {
            payments =  _entityManager.createQuery(
                            "SELECT bp FROM BillPayment bp ORDER BY bp.paymentDate DESC "
                            , BillPayment.class)
                    .getResultList();
        }

        else if(!_securityContext.isCallerInRole("Finance") && !_securityContext.isCallerInRole("Accounting") && !_securityContext.isCallerInRole("Executive"))
        {
            return new ArrayList<>();
        }

        return payments;
    }

    public List<BillPayment> findAllByBillId(Long billId) {
        return _entityManager.createQuery(
                "SELECT bp "
                        + " FROM BillPayment bp "
                        + " WHERE bp.billToPay.id = :billId "
                        + " ORDER BY bp.paymentDate DESC "
                , BillPayment.class)
                .setParameter("billId", billId)
                .getResultList();
    }

    public void create(BillPayment newBillPayment) {
        String username = _securityContext.getCallerPrincipal().getName();
        // Subtract the BillPayment paymentAmount from the Bill paymentBalance
        Bill existingBill = newBillPayment.getBillToPay();
        existingBill.setPaymentBalance(existingBill.getPaymentBalance().subtract(newBillPayment.getPaymentAmount()));
        // Set the payment to the current date
        newBillPayment.setAuthenticatedUsername(username);
        newBillPayment.setPaymentDate(LocalDate.now());
        // Save the newBillPayment
        _entityManager.persist(newBillPayment);
        // Update the existingBill
        _entityManager.merge(existingBill);
    }

    public void update(BillPayment updatedBillPayment) {
        Optional<BillPayment> optionalBillPayment = findOneById(updatedBillPayment.getId());
        if (optionalBillPayment.isPresent()) {
            BillPayment existingBillPayment = optionalBillPayment.get();

            // Update the amountBalance on the Bill by adding the previous paymentAmount
            // and subtract the new paymentAmount
            Bill existingBill = existingBillPayment.getBillToPay();
            BigDecimal previousPaymentAmount = existingBillPayment.getPaymentAmount();
            BigDecimal newPaymentAmount = updatedBillPayment.getPaymentAmount();
            BigDecimal paymentAmountChange = newPaymentAmount.subtract(previousPaymentAmount);
            BigDecimal newAmountBalance = existingBill.getPaymentBalance().subtract(paymentAmountChange);
            existingBill.setPaymentBalance(newAmountBalance);
            _entityManager.merge(existingBill);

            existingBillPayment.setPaymentAmount(updatedBillPayment.getPaymentAmount());
            existingBillPayment.setPaymentDate(updatedBillPayment.getPaymentDate());
            existingBillPayment.setVersion(updatedBillPayment.getVersion());

            // Update the existingBillPayment
            _entityManager.merge(existingBillPayment);
            _entityManager.flush();
        }
    }

    public void delete(Long id) {
        Optional<BillPayment> optionalBillPayment = findOneById(id);
        if (optionalBillPayment.isPresent()) {
            BillPayment existingBillPayment = optionalBillPayment.get();

            // Add the paymentAmount from the Bill
            Bill existingBill = existingBillPayment.getBillToPay();
            existingBill.setPaymentBalance(existingBill.getPaymentBalance().add(existingBillPayment.getPaymentAmount()));
            _entityManager.merge(existingBill);
            // Remove the Bill
            _entityManager.remove(existingBillPayment);
        }
    }
}