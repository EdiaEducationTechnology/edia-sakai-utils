package nl.edia.sakai.secutity;

import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;

public abstract class  SecurityAdvisorTemplate<T> {
	
	protected static final SecurityAdvisor SECURITY_ADVISOR = new SecurityAdvisor(){
		public SecurityAdvice isAllowed(String userId, String function, String reference) {
		    return SecurityAdvice.PASS;
		}
	};
	
	SecurityService securityService;
	SecurityAdvisor securityAdvisor = SECURITY_ADVISOR;
	
	public SecurityAdvisorTemplate(SecurityAdvisor securityAdvisor) {
	    this(org.sakaiproject.authz.cover.SecurityService.getInstance(), securityAdvisor);
    }

	public SecurityAdvisorTemplate() {
		this(org.sakaiproject.authz.cover.SecurityService.getInstance());
    }
	
	public SecurityAdvisorTemplate(SecurityService securityService) {
		this(securityService, SECURITY_ADVISOR);
	}

	public SecurityAdvisorTemplate(SecurityService securityService, SecurityAdvisor securityAdvisor) {
	    super();
	    this.securityService = securityService;
	    this.securityAdvisor = securityAdvisor;
    }

	public T run() {
		// Get the current session
		securityService.pushAdvisor(securityAdvisor);
		try {
			// Run the templated code
			return runAs();
		} finally {
			// Remove the advisor
			securityService.popAdvisor();
		}

	}
	
	public abstract T runAs();

	public SecurityAdvisor getSecurityAdvisor() {
    	return securityAdvisor;
    }

	public void setSecurityAdvisor(SecurityAdvisor securityAdvisor) {
    	this.securityAdvisor = securityAdvisor;
    }

	public SecurityService getSecurityService() {
    	return securityService;
    }

	public void setSecurityService(SecurityService securityService) {
    	this.securityService = securityService;
    }

}
