package com.rickshaw.Repositary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rickshaw.Model.TripBooking;

@Repository
public interface TripBookingRepo extends JpaRepository<TripBooking, Integer>{

}
