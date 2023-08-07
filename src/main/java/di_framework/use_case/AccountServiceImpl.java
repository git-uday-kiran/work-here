package di_framework.use_case;

import di_framework.framework.annotations.Component;

@Component
public class AccountServiceImpl implements AccountService {
	@Override
	public Long getAccountNumber(String userName) {
		return 1234567890L;
	}
}
