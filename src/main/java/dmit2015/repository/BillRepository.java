package dmit2015.repository;

import dmit2015.entity.Bill;
import dmit2015.security.BillSecurityInterceptor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.SecurityContext;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
@Interceptors(BillSecurityInterceptor.class)
public class BillRepository  {

    @Inject
    private SecurityContext _securityContext;

    @PersistenceContext
    private EntityManager _entityManager;

    // TODO: On
    public void create(Bill newBill)
    {
        // TODO: Set the username of the newBill using the username of the authenticated user
        String username = _securityContext.getCallerPrincipal().getName();
        newBill.setUsername(username);
        newBill.setPaymentBalance(newBill.getPaymentDue());
        _entityManager.persist(newBill);
    }

    private void remove(Bill existingBill) {
        // Delete all the BillPayment associated with the existingBill
        _entityManager.createQuery("DELETE FROM BillPayment bp WHERE bp.billToPay.id = :billIdValue")
                .setParameter("billIdValue", existingBill.getId())
                .executeUpdate();
        // Delete the existingBill
        _entityManager.remove(existingBill);
    }

    public void delete(Long billId) {
        Optional<Bill> optionalBill = findOneById(billId);
        if (optionalBill.isPresent()) {
            Bill existingBill = optionalBill.get();
            remove(existingBill);
        }
    }

    public List<Bill> findAll() {
        List<Bill> queryResultList = null;

        /* TODO: Modify the code as follows:
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
            queryResultList = _entityManager.createQuery(
                            "SELECT b FROM Bill b WHERE b.username = :usernameValue", Bill.class)
                    .setParameter("usernameValue", username).getResultList();
        }

        if (_securityContext.isCallerInRole("Accounting") || _securityContext.isCallerInRole("Executive")) {
            queryResultList =  _entityManager.createQuery("""
                        SELECT b
                        FROM Bill b
                        ORDER BY b.dueDate DESC
                                """, Bill.class)
                    .getResultList();
        }

        else if(!_securityContext.isCallerInRole("Finance") && !_securityContext.isCallerInRole("Accounting") && !_securityContext.isCallerInRole("Executive"))
        {
            return new ArrayList<>();
        }

        return queryResultList;
    }

    public Optional<Bill> findOneById(Long billId) {
        Optional<Bill> optionalSingleResult = Optional.empty();
        Bill querySingleResult = _entityManager.find(Bill.class, billId);
        if (querySingleResult != null) {
            optionalSingleResult = Optional.of(querySingleResult);
        }
        return optionalSingleResult;
    }

    public void update(Bill existingBill) {
        Optional<Bill> optionalBill = findOneById(existingBill.getId());
        if (optionalBill.isPresent()) {
            Bill editBill = optionalBill.get();
            editBill.setPayeeName(existingBill.getPayeeName());
            editBill.setDueDate(existingBill.getDueDate());
            editBill.setPaymentDue(existingBill.getPaymentDue());
            editBill.setPaymentBalance(existingBill.getPaymentBalance());
            editBill.setVersion(existingBill.getVersion());
            _entityManager.merge(editBill);
            _entityManager.flush();
        }
    }
}