package com.mypro.context;

import com.mypro.model.admin.SysUser;

public class SysUserContext implements AutoCloseable{
	private static SysUser user = new SysUser();
	private static final ThreadLocal<SysUser> current = new ThreadLocal<SysUser>(){
		protected synchronized SysUser initialValue() {
            return user;
        }
	};
	
	public SysUserContext(SysUser user){
		SysUserContext.user = user;
		current.set(user);
	}
	
	public static SysUser getCurrentSysUser() {
        return current.get();
    }
	
	@Override
	public void close() throws Exception {
		current.remove();
	}

}
