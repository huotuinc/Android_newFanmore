package cy.com.morefan.service;

import cy.com.morefan.listener.DataListener;

public class BaseService {
	protected DataListener listener;
	protected BaseService(DataListener listener){
		this.listener = listener;
	}

}
