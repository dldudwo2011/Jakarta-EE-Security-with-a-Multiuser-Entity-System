package dmit2015.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
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
@Table(name = "assignment08BillPayment")
public class BillPayment implements Serializable {

    // TODO: Add a data field for the username
    @Column(length=32, nullable = false)
    private String authenticatedUsername;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    protected Long id;

    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill billToPay;

    @Column(nullable = false)
    @NotNull(message = "Please enter the payment amount")
    @DecimalMin(value = "0.01", message = "Please enter a payment amount greater or equal to ${value}")
    private BigDecimal paymentAmount;

    @FutureOrPresent(message = "Payment due date must be in the future or present day", groups = {NewBillChecks.class})
    @Column(nullable = false)
    private LocalDate paymentDate = LocalDate.now();

    @Column(nullable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime lastModified;

    @Version
    private Integer version;

    @PrePersist
    private void beforePersist() {
        created = LocalDateTime.now();
        lastModified = created;
    }

    @PreUpdate
    private void beforeUpdate() {
        lastModified = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BillPayment that = (BillPayment) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
