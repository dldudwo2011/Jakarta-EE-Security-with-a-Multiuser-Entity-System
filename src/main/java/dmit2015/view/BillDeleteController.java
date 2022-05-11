package dmit2015.view;

import dmit2015.entity.Bill;
import dmit2015.repository.BillRepository;
import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;

import java.io.Serializable;
import java.util.Optional;

@Named("currentBillDeleteController")
@ViewScoped
public class BillDeleteController implements Serializable {
    private static final long serialVersionUID = 1L;


    @Inject
    private BillRepository _billRepository;

    @Inject
    @ManagedProperty("#{param.editId}")
    @Getter
    @Setter
    private String editId;

    @Getter
    private Optional<Bill> existingBill;

    @PostConstruct
    public void init() {
        if (!Faces.isPostback()) {
            if (editId != null) {
                existingBill = _billRepository.findOneById(Long.parseLong((editId)));
                if (existingBill == null) {
                    Faces.redirect(Faces.getRequestURI().substring(0, Faces.getRequestURI().lastIndexOf("/")) + "/index");
                }
            } else {
                Faces.redirect(Faces.getRequestURI().substring(0, Faces.getRequestURI().lastIndexOf("/")) + "/index");
            }
        }
    }

    public String onDelete() {
        String nextPage = "";
        try {
            _billRepository.delete(Long.parseLong((editId)));
            Messages.addFlashGlobalInfo("Delete was successful.");
            nextPage = "index?faces-redirect=true";
        } catch (Exception e) {
            e.printStackTrace();
            Messages.addGlobalError("Delete was not successful.");
        }
        return nextPage;
    }
}