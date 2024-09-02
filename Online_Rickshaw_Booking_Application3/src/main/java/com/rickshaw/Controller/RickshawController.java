package com.rickshaw.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rickshaw.Exception.CabException;
import com.rickshaw.Exception.CurrentUserSessionException;
import com.rickshaw.Model.Rickshaw;
import com.rickshaw.Service.RickshawService;

@RestController
@RequestMapping("/cab")
public class RickshawController {
	
	@Autowired
	private RickshawService rickshawService;
	
	@PostMapping("/register")
	public ResponseEntity<Rickshaw> register(@RequestBody Rickshaw rickshaw) throws CabException{
		return new ResponseEntity<Rickshaw>(rickshawService.insertCab(rickshaw),HttpStatus.CREATED);
	}
	
	@PutMapping("/update")
	public ResponseEntity<Rickshaw> update(@RequestBody Rickshaw rickshaw,@RequestParam("uuid") String uuid) throws CabException, CurrentUserSessionException{
		return new ResponseEntity<Rickshaw>(rickshawService.updateCab(rickshaw, uuid),HttpStatus.OK);
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<Rickshaw> deleteCab(@RequestParam("cabId") Integer cabId,@RequestParam("uuid") String uuid) throws CabException, CurrentUserSessionException{
		return new ResponseEntity<Rickshaw>(rickshawService.deleteCab(cabId, uuid),HttpStatus.OK);
	}
	
	@GetMapping("/viewCabsOfType/{carType}")
	public ResponseEntity<List<Rickshaw>> viewCabsOfType(@PathVariable("carType") String carType,@RequestParam("uuid") String uuid) throws CabException, CurrentUserSessionException{
		return new ResponseEntity<List<Rickshaw>>(rickshawService.viewCabsOfType(carType, uuid),HttpStatus.OK);
	}
	
	@GetMapping("/countCabsOfType/{carType}")
	public ResponseEntity<Integer> countCabsOfType(@PathVariable("carType") String carType,@RequestParam("uuid") String uuid) throws CabException, CurrentUserSessionException{
		return new ResponseEntity<Integer>(rickshawService.countCabsOfType(carType, uuid),HttpStatus.OK);
	}
}


	
