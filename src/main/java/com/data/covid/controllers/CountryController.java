package com.data.covid.controllers;

import com.data.covid.models.Country;
import com.data.covid.models.CountryModel;
import com.data.covid.payload.response.MessageResponse;
import com.data.covid.services.CountryService;
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
@RequestMapping("/country")
public class CountryController {
    public final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    // return all Countries

    @GetMapping(value={"/","/list",""} )
    @PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
    public @ResponseBody List<Country> list(){
        return countryService.list();
    }

    // return Country by id

    @GetMapping(value="/findById")
    @PreAuthorize("hasRole('ROLE_USER')  or hasRole('ROLE_ADMIN')")
    public @ResponseBody  Country findById(@RequestParam("id")  Long id){
        return countryService.findById(id);
    }

    // return Country by name

    @GetMapping(value="/findByName")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public @ResponseBody  Country findByName(@ModelAttribute CountryModel countryModel) {
        return countryService.findByName(countryModel);
    }

    // add new Country

    @PutMapping(value="/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> add(@ModelAttribute CountryModel countryModel){
        return countryService.add(countryModel);
    }

    // edit/update a Country record - only if record with id exists

    @PostMapping(value="/update")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> update(@ModelAttribute Country country) {
        return countryService.update( country.getId(), country );
    }

    // delete by id

    @DeleteMapping(value="/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> delete(@ModelAttribute Country country){
        return countryService.delete(country);
    }
}
