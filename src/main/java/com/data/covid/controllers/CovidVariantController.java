package com.data.covid.controllers;



import com.data.covid.models.CovidVariant;
import com.data.covid.models.CovidVariantModel;
import com.data.covid.payload.response.MessageResponse;
import com.data.covid.repositories.CountryRepository;
import com.data.covid.services.CovidVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Darren O'Donnell
 */
@Controller
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/covidVariant")
public class CovidVariantController {

    public final CovidVariantService covidVariantService;
    private final CountryRepository countryRepository;

    @Autowired
    public CovidVariantController(CovidVariantService covidVariantService,
                                  CountryRepository countryRepository) {
        this.covidVariantService = covidVariantService;
        this.countryRepository = countryRepository;
    }

    // return all CovidVariants

    @GetMapping(value={"/","/list",""} )
    @PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
    public @ResponseBody List<CovidVariant> list(){
        return covidVariantService.list();
    }

    // return CovidVariant by id

    @GetMapping(value="/findById")
    @PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
    public @ResponseBody
    CovidVariant findById(@RequestParam("id")  Long id){
        return covidVariantService.findById(id);
    }

    // return CovidVariant by name

    @GetMapping(value="/findByName")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public @ResponseBody List<CovidVariant> findByName(@ModelAttribute CovidVariantModel covidVariantModel) {
        return covidVariantService.findByName(covidVariantModel);
    }

    @GetMapping(value="/findByNameAndCountryCode")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public @ResponseBody CovidVariant findByNameAndCountryCode(@ModelAttribute CovidVariantModel covidVariantModel, @ModelAttribute("country_code") String countryCode) {
        return covidVariantService.findByNameAndCountryCode(covidVariantModel, countryCode);
    }
    @GetMapping(value="/findByNameAndCountryName")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public @ResponseBody CovidVariant findByNameAndCountryName(@ModelAttribute CovidVariantModel covidVariantModel, @ModelAttribute("country_name") String countryName) {
        return covidVariantService.findByNameAndCountryName(covidVariantModel, countryName);
    }

    // add new CovidVariant

    @PutMapping(value="/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> add(@ModelAttribute CovidVariantModel covidVariantModel, @ModelAttribute("country_code") String countryCode ){
        return covidVariantService.add(covidVariantModel, countryCode);
    }

    // edit/update a CovidVariant record - only if record with id exists

    @PostMapping(value="/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> update(@ModelAttribute CovidVariant covidVariant, @ModelAttribute("country_code") String countryCode ) {
        return covidVariantService.update( covidVariant.getId(), covidVariant, countryCode );
    }


    // delete by id

    @DeleteMapping(value="/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> delete(@ModelAttribute CovidVariant covidVariant){
        return covidVariantService.delete(covidVariant);
    }
}
