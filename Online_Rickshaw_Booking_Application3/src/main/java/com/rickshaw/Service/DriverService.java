package com.rickshaw.Service;

import java.util.List;

import com.rickshaw.Exception.CurrentUserSessionException;
import com.rickshaw.Exception.DriverException;
import com.rickshaw.Model.Driver;

public interface DriverService {

    Driver insertDriver(Driver driver)throws DriverException;
	
	Driver updateDriver(Driver driver,String uuid)throws DriverException,CurrentUserSessionException;
	
	Driver deleteDriver(Integer driverId, String uuid)throws DriverException,CurrentUserSessionException;
	
	List<Driver> viewBestDriver(String uuid)throws DriverException,CurrentUserSessionException;
	
	Driver viewDriver(Integer driverId,String uuid)throws DriverException,CurrentUserSessionException;
	
}
