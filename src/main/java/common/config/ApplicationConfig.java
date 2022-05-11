package common.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.annotation.sql.DataSourceDefinitions;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.annotation.FacesConfig;
import jakarta.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.LoginToContinue;
import jakarta.security.enterprise.identitystore.LdapIdentityStoreDefinition;

// TODO: Set the Jakarta EE Authentication Mechanism

@CustomFormAuthenticationMechanismDefinition(
		loginToContinue = @LoginToContinue(
				loginPage="/customLogin.xhtml",
				useForwardToLogin = false,
				errorPage=""
		)
)

// TODO: Set the Jakarta EE Identity Store

@LdapIdentityStoreDefinition(
		url = "ldap://192.168.88.138:389",
		callerSearchBase = "ou=Departments,dc=dmit2015,dc=ca",
		callerNameAttribute = "SamAccountName", // SamAccountName or UserPrincipalName
		groupSearchBase = "ou=Departments,dc=dmit2015,dc=ca",
		bindDn = "cn=DAUSTIN,ou=IT,ou=Departments,dc=dmit2015,dc=ca",
		bindDnPassword = "Password2015",
		priority = 5
)



@DataSourceDefinitions({

		@DataSourceDefinition(
			name="java:app/datasources/mssqlDS",
			className="com.microsoft.sqlserver.jdbc.SQLServerDataSource",
			url="jdbc:sqlserver://DMIT-Capstone1.ad.sast.ca;databaseName=DMIT2015_1212_E01_ylee39;TrustServerCertificate=true",   // change A01 to E01 if you are in section E01, change yourNaitUsername
			user="ylee39",
			password="RemotePassword200441266"),

//			name="java:app/datasources/mssqlDS",
//			className="com.microsoft.sqlserver.jdbc.SQLServerDataSource",
//			url="jdbc:sqlserver://localhost;databaseName=DMIT2015_1212_CourseDB;TrustServerCertificate=true",
//			user="user2015",
//			password="Password2015"),

})

@FacesConfig
@ApplicationScoped
public class ApplicationConfig {

}