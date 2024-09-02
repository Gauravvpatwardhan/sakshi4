package com.rickshaw.Service;

import java.util.List;

import com.rickshaw.Exception.CabException;
import com.rickshaw.Exception.CurrentUserSessionException;
import com.rickshaw.Exception.TripBookingException;
import com.rickshaw.Model.Rickshaw;
import com.rickshaw.Model.TripBooking;
import com.rickshaw.Model.TripBookingDTO;

public interface TripBookingService {

	List<Rickshaw> searchByLocation(String pickUpLocation,String uuid)throws TripBookingException,CurrentUserSessionException;
	
	TripBooking BookRequest(Integer cabId,TripBooking tripBooking,String uuid) throws TripBookingException,CabException,CurrentUserSessionException;
	
	TripBooking AssignDriverByAdmin(Integer TripBookingId,String uuid)throws TripBookingException,CabException,CurrentUserSessionException;
	
	TripBookingDTO viewBookingById(Integer TripBookingId,String uuid )throws TripBookingException,CabException,CurrentUserSessionException;
	
	String MarkTripAsCompleted(Integer TripBookingId,String uuid)throws TripBookingException,CurrentUserSessionException;;
}
