package dmit2015.security;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

public class BillSecurityInterceptor {

    @Inject
    private Security _security;

    @AroundInvoke
    private Object logMethod(InvocationContext ic) throws Exception {
        String methodName = ic.getMethod().getName();
        if (methodName.startsWith("create") || methodName.startsWith("update")) {
            boolean isNotInApprovedRole = !_security.isInAnyRole("Finance");
            if (isNotInApprovedRole) {
                throw new RuntimeException("Access denied. You do not have permission to create a bill");
            }
        }

        if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            boolean isNotInApprovedRole = !_security.isInAnyRole("Accounting");
            if (isNotInApprovedRole) {
                throw new RuntimeException("Access denied. You do not have permission to delete a bill");
            }
        }

        if (methodName.startsWith("findOneById")) {
            boolean isNotInApprovedRole = !_security.isInAnyRole("Accounting","Finance","Executive");
            if (isNotInApprovedRole) {
                throw new RuntimeException("Access denied. You do not have permission to delete a bill payment");
            }
        }

        return ic.proceed();
    }
}
