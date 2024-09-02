package com.rickshaw.Service;

import java.lang.StackWalker.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rickshaw.Exception.CabException;
import com.rickshaw.Exception.CurrentUserSessionException;
import com.rickshaw.Model.Rickshaw;
import com.rickshaw.Model.CurrentUserSession;
import com.rickshaw.Repositary.RickshawRepo;
import com.rickshaw.Repositary.CurrentUserSessionRepo;

@Service
public class RickshawServiceImpl implements RickshawService{

	@Autowired
	private RickshawRepo rickshawRepo;
	
	@Autowired
	private CurrentUserSessionRepo currRepo;
	
	@Override
	public Rickshaw insertCab(Rickshaw rickshaw) throws CabException {
		
		Optional<Rickshaw> findCab = rickshawRepo.findByCarNumber(rickshaw.getCarNumber());
		if(findCab.isEmpty()) {
			return rickshawRepo.save(rickshaw);
		}
		else {
			throw new CabException("Rickshaw is already Registered");
		}
	}

	@Override
	public Rickshaw updateCab(Rickshaw rickshaw, String uuid) throws CabException, CurrentUserSessionException {
		
		Optional<CurrentUserSession> validuser = currRepo.findByUuidAndRole(uuid);
		if(validuser.isPresent()) {
			Optional<Rickshaw> cb = rickshawRepo.findByCarNumber(rickshaw.getCarNumber());
			if(cb.isPresent()) {
				
				Rickshaw data = cb.get();
				data.setCarName(rickshaw.getCarName());
				data.setCarNumber(rickshaw.getCarNumber());
				data.setCarType(rickshaw.getCarType());
				data.setPerKmRate(rickshaw.getPerKmRate());
				
				return rickshawRepo.save(data);
				
			}
			else {
				throw new CabException("Rickshaw is not Registered");
			}
		}
		else {
			throw new CurrentUserSessionException("User not login In or User is not an Admin");
		}
	}

	@Override
	public Rickshaw deleteCab(Integer cabId, String uuid) throws CabException, CurrentUserSessionException {
		
		Optional<CurrentUserSession> validuser = currRepo.findByUuidAndRole(uuid);
		if(validuser.isPresent()) {
			Optional<Rickshaw> cb = rickshawRepo.findById(cabId);
			if(cb.isPresent()) {
				Rickshaw rickshaw = cb.get();
				rickshawRepo.delete(rickshaw);
				return rickshaw;
			}
			else {
				throw new CabException("Rickshaw is not Registered");
			}
		}
		else {
			throw new CurrentUserSessionException("User not login In or User is not an Admin");
		}
	}

	@Override
	public List<Rickshaw> viewCabsOfType(String carType, String uuid) throws CabException, CurrentUserSessionException {
		
		Optional<CurrentUserSession> validuser = currRepo.findByUuidAndRole(uuid);
		if(validuser.isPresent()) {
			List<Rickshaw> allCabs = rickshawRepo.findAll();
			List<Rickshaw> viewCabsOfType = new ArrayList<>();

			for(Rickshaw rickshaw : allCabs) {
				if(rickshaw.getCarType().equalsIgnoreCase(carType)) {
					viewCabsOfType.add(rickshaw);
				}
			}
			if(viewCabsOfType.isEmpty()) {
				throw new CabException("No Rickshaw is Registered");
			}
			else {
				return viewCabsOfType;
			}
		}
		else {
			throw new CurrentUserSessionException("User not login In or User is not an Admin");
		}
	}

	@Override
	public Integer countCabsOfType(String carType, String uuid) throws CabException, CurrentUserSessionException {
		Optional<CurrentUserSession> validuser = currRepo.findByUuidAndRole(uuid);
		if(validuser.isPresent()) {
			List<Rickshaw> allCabs = rickshawRepo.findAll();
			List<Rickshaw> viewCabsOfType = new ArrayList<>();
			Integer countCabsOfType = 0;

			for(Rickshaw rickshaw : allCabs) {
				if(rickshaw.getCarType().equalsIgnoreCase(carType)) {
					countCabsOfType++;
				}
			}
			if(viewCabsOfType.isEmpty()) {
				throw new CabException("No Rickshaw found with the given type");
			}
			else {
				return countCabsOfType;
			}
		}
		else {
			throw new CurrentUserSessionException("User not login In or User is not an Admin");
		}
		
	}

}
