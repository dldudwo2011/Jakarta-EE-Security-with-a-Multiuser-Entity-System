package dmit2015.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "assignment08Bill")
public class Bill implements Serializable {

    // TODO: Add a data field for the username
    @Column(length=32, nullable = false)
    private String username;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    protected Long id;

    @NotBlank(message = "Please enter a payee name")
    protected String payeeName;

    @NotNull(message = "Please enter/select the due date")
    @FutureOrPresent(message = "Payment due date must be in the future or present day", groups = {NewBillChecks.class})
    protected LocalDate dueDate = LocalDate.now().plusWeeks(2);

    @NotNull(message = "Please enter the amount that is due.")
    protected BigDecimal paymentDue;

    protected BigDecimal paymentBalance;

    @Column(nullable = false)
    private LocalDateTime createdDateTime;

    @Column(nullable = false)
    private LocalDateTime lastModifiedDateTime;

    @Version
    private Integer version;

    @PrePersist
    private void beforePersist() {
        createdDateTime = LocalDateTime.now();
        lastModifiedDateTime = createdDateTime;
    }

    @PreUpdate
    private void beforeUpdate() {
        lastModifiedDateTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Bill bill = (Bill) o;
        return id != null && Objects.equals(id, bill.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
