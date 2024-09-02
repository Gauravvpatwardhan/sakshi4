package com.rickshaw.Service;



import java.util.List;

import com.rickshaw.Exception.CurrentUserSessionException;
import com.rickshaw.Exception.CustomerException;
import com.rickshaw.Model.Customer;

public interface CustomerService {

	Customer insertCustomer(Customer customer) throws CustomerException;
	
	Customer updateCustomer(Customer customer,String uuid) throws CustomerException, CurrentUserSessionException;
	
	Customer deleteCustomer(Integer customerId,String uuid)  throws CustomerException, CurrentUserSessionException;
	
	List<Customer> viewCustomer(String uuid)  throws CustomerException, CurrentUserSessionException;
	
	Customer viewCustomer(Integer customerId, String uuid) throws CustomerException, CurrentUserSessionException;
	

	
	
}
