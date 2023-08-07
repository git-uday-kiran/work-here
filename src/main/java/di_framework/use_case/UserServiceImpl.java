package di_framework.use_case;

import di_framework.framework.annotations.Component;

@Component
public class UserServiceImpl implements UserService {
	@Override
	public String getUserName() {
		return "uday kiran";
	}
}
