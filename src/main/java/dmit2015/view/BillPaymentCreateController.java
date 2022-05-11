package dmit2015.view;

import dmit2015.entity.Bill;
import dmit2015.entity.BillPayment;
import dmit2015.repository.BillPaymentRepository;
import dmit2015.repository.BillRepository;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Named("currentBillPaymentCreateController")
@ViewScoped
public class BillPaymentCreateController implements Serializable {

    @Inject
    private BillPaymentRepository _billPaymentRepository;

    @Inject
    private BillRepository _billRepository;

    @Getter
    private BillPayment newBillPayment = new BillPayment();

    @Getter
    private List<BillPayment> existingBillPayments;

    @Inject
    @ManagedProperty("#{param.billId}")
    @Getter
    @Setter
    private Long billId;

    @PostConstruct
    public void init() {
        if (!Faces.isPostback() && billId != null) {
            Optional<Bill> optionalBill = _billRepository.findOneById(billId);
            if (optionalBill.isPresent()) {
                Bill selectedBill = optionalBill.get();
                newBillPayment.setBillToPay(selectedBill);
                newBillPayment.setPaymentAmount(selectedBill.getPaymentBalance());
            }

            existingBillPayments = _billPaymentRepository.findAllByBillId(billId);
        }
    };

    public String onCreate() {
        String nextPage = "";
        try {
            _billPaymentRepository.create(newBillPayment);
            Messages.addFlashGlobalInfo("Create was successful.");
            nextPage = "/bills/index?faces-redirect=true";
        } catch (RuntimeException e) {
            Messages.addGlobalWarn(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Messages.addGlobalError("Create was not successful. {0}", e.getMessage());
        }
        return nextPage;
    }

}