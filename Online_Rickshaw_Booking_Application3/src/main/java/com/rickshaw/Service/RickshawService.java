package com.rickshaw.Service;

import java.util.List;

import com.rickshaw.Exception.CabException;
import com.rickshaw.Exception.CurrentUserSessionException;
import com.rickshaw.Model.Rickshaw;

public interface RickshawService {

	Rickshaw insertCab(Rickshaw rickshaw)throws CabException;
	
	Rickshaw updateCab(Rickshaw rickshaw,String uuid)throws CabException, CurrentUserSessionException;
	
	Rickshaw deleteCab(Integer cabId,String uuid)  throws CabException, CurrentUserSessionException;
	
	List<Rickshaw> viewCabsOfType(String carType,String uuid)throws CabException, CurrentUserSessionException;
	
	Integer countCabsOfType(String carType,String uuid)throws CabException, CurrentUserSessionException;
	
	
}
