package com.rickshaw.Repositary;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rickshaw.Model.Rickshaw;

public interface RickshawRepo extends JpaRepository<Rickshaw, Integer>{

	Optional<Rickshaw> findByCarNumber(String carNumber);
}
