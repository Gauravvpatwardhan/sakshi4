package com.rickshaw.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rickshaw.Exception.CabException;
import com.rickshaw.Exception.CurrentUserSessionException;
import com.rickshaw.Exception.TripBookingException;
import com.rickshaw.Model.Rickshaw;
import com.rickshaw.Model.CurrentUserSession;
import com.rickshaw.Model.Customer;
import com.rickshaw.Model.Driver;
import com.rickshaw.Model.TripBooking;
import com.rickshaw.Model.TripBookingDTO;
import com.rickshaw.Repositary.RickshawRepo;
import com.rickshaw.Repositary.CurrentUserSessionRepo;
import com.rickshaw.Repositary.CustomerRepo;
import com.rickshaw.Repositary.DriverRepo;
import com.rickshaw.Repositary.TripBookingRepo;

@Service
public class TripBookingServiceImpl implements TripBookingService{

	
	@Autowired
	private TripBookingRepo tripBookingRepo;
	
	@Autowired
	private CustomerRepo customerRepo;
	
	@Autowired
	private RickshawRepo rickshawRepo;
	
	@Autowired
	private CurrentUserSessionRepo currRepo;
	
	@Autowired
	private DriverRepo driverRepo;
	
	
	@Override
	public List<Rickshaw> searchByLocation(String pickUpLocation, String uuid)
			throws TripBookingException, CurrentUserSessionException {
		Optional<CurrentUserSession> validUser = currRepo.findByUuid(uuid);
		if(validUser.isPresent()) {
			List<Rickshaw> allCab = rickshawRepo.findAll();
		    List<Rickshaw> availableCab = new ArrayList<>();
		    for(Rickshaw rickshaw : allCab) {
		    	if(rickshaw.getCabCurrStatus().equalsIgnoreCase("Available") && rickshaw.getCurrLocation().equalsIgnoreCase(pickUpLocation)) {
		    		availableCab.add(rickshaw);
		    	}
		    }
		    if(availableCab.isEmpty()) {
		    	throw new TripBookingException("No Rickshaw Available in this Location");
		    }
		    else {
		    	return availableCab;
		    }
		}
		else {
			throw new CurrentUserSessionException("User Not Login");
		}
	}


	@Override
	public TripBooking BookRequest(Integer cabId, TripBooking tripBooking, String uuid)
			throws TripBookingException ,CabException , CurrentUserSessionException{
		Optional<CurrentUserSession> validUser = currRepo.findByUuid(uuid);
		if(validUser.isPresent()) {
			CurrentUserSession currUser = validUser.get();
			Optional<Customer> cust = customerRepo.findById(currUser.getCurrUserId());
			Customer customer = cust.get();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
			LocalDateTime fromDT = LocalDateTime.parse(tripBooking.getFromDateTime(), formatter);
            LocalDateTime toDT = LocalDateTime.parse(tripBooking.getToDateTime(), formatter);
			List<TripBooking> allTripByCustomer = customer.getTripBooking();
			if(isTripOverlap(tripBooking, allTripByCustomer)==true) {
				throw new TripBookingException("You have already booked an another Trip in the same Time");
			}
			else {
				Optional<Rickshaw> addCab = rickshawRepo.findById(cabId);
				if(addCab.isPresent()) {
					Rickshaw newCab= addCab.get();
					if(newCab.getCabCurrStatus().equalsIgnoreCase("Available") &&
							newCab.getCurrLocation().equalsIgnoreCase(tripBooking.getPickupLocation())) {
						newCab.setCabCurrStatus("Pending");
						tripBooking.setCab(newCab);
						tripBooking.setCustomer(customer);
						tripBooking.setCurrStatus("Pending");
						allTripByCustomer.add(tripBooking);
						customerRepo.save(customer);
						return tripBookingRepo.save(tripBooking);
						
					}
					else {
						throw new CabException("This Rickshaw is not available currently for location or avability purpose");
					}
				}
				else {
					throw new CabException("No Rickshaw Present with the given Credentials");
				}
			}
		}
		else {
			throw new CurrentUserSessionException("User is Not Login");
		}
	}
	
	

	public List<Driver> getAvailableDrivers(String pickUpLocation) {
	    return driverRepo.findByCurrLocationAndCurrDriverStatus(pickUpLocation, "available");
	}

	
	
	public boolean isTripOverlap(TripBooking newTripBooking, List<TripBooking> existingTrips) {
	    if (newTripBooking.getFromDateTime() == null || newTripBooking.getToDateTime() == null) {
	        return false; 
	    }
	    
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
	    LocalDateTime newTripFromDT = LocalDateTime.parse(newTripBooking.getFromDateTime(), formatter);
	    LocalDateTime newTripToDT = LocalDateTime.parse(newTripBooking.getToDateTime(), formatter);
	    for (TripBooking existingTripBooking : existingTrips) {
	        if (existingTripBooking.getFromDateTime() == null || existingTripBooking.getToDateTime() == null) {
	            continue;
	        }
	        LocalDateTime existingTripFromDT = LocalDateTime.parse(existingTripBooking.getFromDateTime(), formatter);
	        LocalDateTime existingTripToDT = LocalDateTime.parse(existingTripBooking.getToDateTime(), formatter);
	        if (newTripFromDT.isBefore(existingTripToDT) && newTripToDT.isAfter(existingTripFromDT)) {
	            return true;
	        }
	    }    
	    return false;
	}



	@Override
	public TripBooking AssignDriverByAdmin(Integer TripBookingId, String uuid)
			throws TripBookingException, CabException, CurrentUserSessionException {
		Optional<CurrentUserSession> validUser = currRepo.findByUuidAndRole(uuid);
		if(validUser.isPresent()) {
			Optional<TripBooking> optionalTrip = tripBookingRepo.findById(TripBookingId);
			if(optionalTrip.isPresent()) {
				TripBooking trip = optionalTrip.get();
			    Customer customer = trip.getCustomer();
			    List<TripBooking> allTrips = customer.getTripBooking();
			    List<Driver> allDrivers = driverRepo.findByCurrLocationAndCurrDriverStatus(trip.getPickupLocation(), "available");
			    if(allDrivers.isEmpty()) {
			    	trip.setCurrStatus("cancelled");
			    	TripBooking canceltrip =  tripBookingRepo.save(trip);
			    	for(TripBooking tb : allTrips) {
			    		if(tb.getTripBookingId()==trip.getTripBookingId()) {
			    			tb.setCurrStatus("cancelled");
			    		}
			    	}
			    	customer.setTripBooking(allTrips);
			    	 throw new TripBookingException("No driver is available for this trip.");
			    }
			    else {
			    	Driver assignDriver = allDrivers.get(0); 
				    assignDriver.setCurrDriverStatus("Booked");
				    assignDriver.setCab(trip.getCab());
                    
				    trip.getCab().setDriver(assignDriver);
				    trip.getCab().setCabCurrStatus("Booked");
				    rickshawRepo.save(trip.getCab());
				    
				    
				    List<TripBooking> allTripByDrv = assignDriver.getTrips();
				    allTripByDrv.add(trip);
				    assignDriver.setTrips(allTripByDrv);

				    trip.setCurrStatus("confirmed");
				    trip.setDriver(assignDriver);

				    List<TripBooking> allTrip = customer.getTripBooking();
				    allTrip.add(trip);

				    tripBookingRepo.save(trip);
				    customerRepo.save(customer);
				    return trip;
			    }
			}
			else {
				throw new TripBookingException("No trip is booked with provided tripBookingId.");
			}
		}
		else {
			throw new CurrentUserSessionException("User is not logged in or is not an admin.");
		}
	}


	@Override
	public TripBookingDTO viewBookingById(Integer TripBookingId, String uuid)
			throws TripBookingException, CabException, CurrentUserSessionException {
		Optional<CurrentUserSession> validUser = currRepo.findByUuid(uuid);
		if(validUser.isPresent()) {
			Optional<TripBooking> tp = tripBookingRepo.findById(TripBookingId);
			if(tp.isPresent()) {
				TripBooking trip = tp.get();
				TripBookingDTO showTrip = new TripBookingDTO();
				showTrip.setTripBookingId(TripBookingId);
				showTrip.setPickupLocation(trip.getPickupLocation());
				showTrip.setFromDateTime(trip.getFromDateTime());
				showTrip.setDropLocation(trip.getDropLocation());
				showTrip.setToDateTime(trip.getToDateTime());
				showTrip.setDistanceInKm(trip.getDistanceInKm());
				showTrip.setDriverName(trip.getDriver().getUserName());
				showTrip.setLicenceNo(trip.getDriver().getLicenceNo());
				showTrip.setRating(trip.getDriver().getRating());
				showTrip.setCarType(trip.getCab().getCarType());
				showTrip.setCarName(trip.getCab().getCarName());
				showTrip.setCarNumber(trip.getCab().getCarNumber());
				showTrip.setPerKmRate(trip.getCab().getPerKmRate());
				showTrip.setFare(trip.getCab().getPerKmRate() * trip.getDistanceInKm());
				showTrip.setTripStatus(trip.getCurrStatus());
				return showTrip;
			}
			else {
				throw new TripBookingException("No trip is booked with provided tripBookingId.");
			}
		}
		else {
			throw new CurrentUserSessionException("User is not logged in");
		}
	    
	}


	@Override
	public String MarkTripAsCompleted(Integer TripBookingId, String uuid)
			throws TripBookingException, CurrentUserSessionException {
		Optional<CurrentUserSession> validUser = currRepo.findByUuid(uuid);
		if(validUser.isPresent()) {
			Optional<TripBooking> tp = tripBookingRepo.findById(TripBookingId);
			if(tp.isPresent()) {
				TripBooking trip = tp.get();
				trip.setCurrStatus("Completed");
				tripBookingRepo.save(trip);
				Customer cust = trip.getCustomer();
				List<TripBooking> allTrip = cust.getTripBooking();
				for(TripBooking tb : allTrip) {
					if(tb.getTripBookingId() == trip.getTripBookingId()) {
						tb.setCurrStatus("completed");
					}
				}
				customerRepo.save(cust);
				trip.getCab().setCabCurrStatus("AVAILABLE");
				rickshawRepo.save(trip.getCab());
				trip.getDriver().setCurrDriverStatus("Available");
				trip.getDriver().setCab(null);
			    trip.getCab().setDriver(null);
				driverRepo.save(trip.getDriver());
				return "Thank you your Trip has been Completed";
			}
			else {
				throw new TripBookingException("No trip is booked with provided tripBookingId.");
			}
		}
		else {
			throw new CurrentUserSessionException("User is not logged in");
		}
	}


	

	

}




