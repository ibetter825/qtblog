package com.mypro.context;

import com.mypro.model.front.QtAccount;


public class QtAccountContext implements AutoCloseable{
	static final ThreadLocal<QtAccount> current = new ThreadLocal<QtAccount>();
	
	public QtAccountContext(QtAccount account){
		current.set(account);
	}
	
	public static QtAccount getCurrentQtAccount() {
        return current.get();
    }
	
	@Override
	public void close() throws Exception {
		current.remove();
	}

}
