package com.data.covid.services;


import com.data.covid.enums.MessageTypes;
import com.data.covid.exceptions.MyMessageResponse;
import com.data.covid.models.Country;
import com.data.covid.models.CountryModel;
import com.data.covid.payload.response.MessageResponse;
import com.data.covid.repositories.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class CountryService {

    CountryRepository countryRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    // return all Countrys

    public List<Country> list(){
        List<Country> countries = countryRepository.findAll();
        if(countries.isEmpty()) new MyMessageResponse("Error: No Countries listed", MessageTypes.WARN);
        return countries;
    }

    // return Country by id

    public Country findById( Long id){
        Optional<Country> country = countryRepository.findById(id);
        if(country.isEmpty())
            new MyMessageResponse(String.format("Country id: %d not found", id), MessageTypes.ERROR);
        return country.orElse(new Country());
    }

    // return Country by name

    public Country findByName( CountryModel countryModel) {
        Optional<Country> country = countryRepository.findByName(countryModel.getName());
        if(country.isEmpty()) new MyMessageResponse(String.format("Country name: %s not found", countryModel.getName()), MessageTypes.INFO);
        return country.orElse(new Country());
    }

    // add new Country

    public ResponseEntity<MessageResponse> add(CountryModel countryModel){

        if(countryRepository.existsByName(countryModel.getName()))
            return ResponseEntity.ok(new MyMessageResponse("Error: Country already exists", MessageTypes.WARN));

        countryRepository.save(countryModel.translateModelToCountry());
        return ResponseEntity.ok(new MyMessageResponse("new Country added", MessageTypes.INFO));
    }

    // delete by id

    public ResponseEntity<MessageResponse> delete( Country country){
        Long id = country.getId();
        if(!countryRepository.existsById(id))
            return ResponseEntity.ok(new MyMessageResponse("Error: Cannot delete Country with id: "+id, MessageTypes.WARN));

        countryRepository.deleteById(id);
        return ResponseEntity.ok(new MyMessageResponse("Country deleted with id: " + id, MessageTypes.INFO));
    }

    // edit/update a Country record - only if record with id exists

    public ResponseEntity<MessageResponse> update(Long id, Country country){

        // check if exists first
        // then update

        if(!countryRepository.existsById(id))
            return ResponseEntity.ok(new MyMessageResponse("Error: Id does not exist ["+id+"] -> cannot update record", MessageTypes.WARN));

        countryRepository.save(country);
        return ResponseEntity.ok(new MyMessageResponse("Country record updated", MessageTypes.INFO));
    }


}
