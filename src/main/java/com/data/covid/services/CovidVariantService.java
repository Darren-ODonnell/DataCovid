package com.data.covid.services;


import com.data.covid.enums.MessageTypes;
import com.data.covid.exceptions.MyMessageResponse;
import com.data.covid.models.Country;
import com.data.covid.models.CovidVariant;
import com.data.covid.models.CovidVariantModel;
import com.data.covid.payload.response.MessageResponse;
import com.data.covid.repositories.CountryRepository;
import com.data.covid.repositories.CovidVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CovidVariantService {

    CovidVariantRepository covidVariantRepository;
    CountryRepository countryRepository;

    @Autowired
    public CovidVariantService(CovidVariantRepository covidVariantRepository,CountryRepository countryRepository) {
        this.covidVariantRepository = covidVariantRepository;
        this.countryRepository = countryRepository;
    }

    // return all CovidVariants

    public List<CovidVariant> list(){
        List<CovidVariant> covidVariants = covidVariantRepository.findAll();
        if(covidVariants.isEmpty()) new MyMessageResponse("Error: No CovidVariants listed", MessageTypes.WARN);
        return covidVariants;
    }

    // return CovidVariant by id

    public CovidVariant findById( Long id){
        Optional<CovidVariant> covidVariant = covidVariantRepository.findById(id);
        if(covidVariant.isEmpty())
            new MyMessageResponse(String.format("CovidVariant id: %d not found", id), MessageTypes.ERROR);
        return covidVariant.orElse(new CovidVariant());
    }

    // return CovidVariant by name

    public List<CovidVariant> findByName( CovidVariantModel covidVariantModel) {
        Optional<List<CovidVariant>> covidVariants = covidVariantRepository.findByName(covidVariantModel.getName());
        if(covidVariants.isEmpty()) new MyMessageResponse(String.format("CovidVariant name: %s not found", covidVariantModel.getName()), MessageTypes.INFO);
        return covidVariants.orElse(new ArrayList<CovidVariant>());
    }

    // return CovidVariant by name and country code

    public CovidVariant findByNameAndCountryCode( CovidVariantModel covidVariantModel, String countryCode) {
        Optional<Country> country = countryRepository.findByCode(countryCode);


        Optional<CovidVariant> covidVariant = covidVariantRepository.findByNameAndCountryCode(covidVariantModel.getName(), country.get());
        if(covidVariant.isEmpty()) new MyMessageResponse(String.format("CovidVariant name: %s not found", covidVariantModel.getName()), MessageTypes.INFO);
        return covidVariant.orElse(new CovidVariant());
    }

    // return CovidVariant by name and country name

    public CovidVariant findByNameAndCountryName(CovidVariantModel covidVariantModel, String countryName) {
        Optional<Country> country = countryRepository.findByName(countryName);

        Optional<CovidVariant> covidVariant = covidVariantRepository.findByNameAndCountryCode(covidVariantModel.getName(), country.get());
        if(covidVariant.isEmpty()) new MyMessageResponse(String.format("CovidVariant name: %s not found", covidVariantModel.getName()), MessageTypes.INFO);
        return covidVariant.orElse(new CovidVariant());
    }

    // add new CovidVariant

    public ResponseEntity<MessageResponse> add(CovidVariantModel covidVariantModel, String countryCode){

        Optional<Country> country = countryRepository.findByCode(countryCode);
        covidVariantModel.setCountryCode(country.get());

        if(covidVariantRepository.existsByName(covidVariantModel.getName()))
            return ResponseEntity.ok(new MyMessageResponse("Error: CovidVariant already exists", MessageTypes.WARN));

        covidVariantRepository.save(covidVariantModel.translateModelToCovidVariant());
        return ResponseEntity.ok(new MyMessageResponse("new CovidVariant added", MessageTypes.INFO));
    }

    // delete by id

    public ResponseEntity<MessageResponse> delete( CovidVariant covidVariant){
        Long id = covidVariant.getId();
        if(!covidVariantRepository.existsById(id))
            return ResponseEntity.ok(new MyMessageResponse("Error: Cannot delete CovidVariant with id: "+id, MessageTypes.WARN));

        covidVariantRepository.deleteById(id);
        return ResponseEntity.ok(new MyMessageResponse("CovidVariant deleted with id: " + id, MessageTypes.INFO));
    }

    // edit/update a CovidVariant record - only if record with id exists

    public ResponseEntity<MessageResponse> update(Long id, CovidVariant covidVariant, String countryCode){
        Optional<Country> country = countryRepository.findByCode(countryCode);
        covidVariant.setCountryCode(country.get());
        // check if exists first
        // then update

        if(!covidVariantRepository.existsById(id))
            return ResponseEntity.ok(new MyMessageResponse("Error: Id does not exist ["+id+"] -> cannot update record", MessageTypes.WARN));

        covidVariantRepository.save(covidVariant);
        return ResponseEntity.ok(new MyMessageResponse("CovidVariant record updated", MessageTypes.INFO));
    }



}
