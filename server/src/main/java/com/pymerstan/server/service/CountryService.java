package com.pymerstan.server.service;

import com.pymerstan.server.dto.CountryDto;
import com.pymerstan.server.entity.Country;
import com.pymerstan.server.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public List<CountryDto> getVisibleCountries() {
        return countryRepository.findByVisibleTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<CountryDto> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public CountryDto createCountry(CountryDto request) {
        if (countryRepository.existsById(request.getId())) {
            throw new IllegalArgumentException("A country with this ID already exists.");
        }

        Country country = new Country();
        country.setId(request.getId());
        country.setName(request.getName());
        country.setVisible(request.getVisible());

        return mapToDto(countryRepository.save(country));
    }

    public CountryDto updateCountry(Integer id, CountryDto request) {
        if (id!=request.getId()){
            throw new RuntimeException("ids must match");
        }
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Country not found."));

        // We do not update the ID itself, only the data
        country.setName(request.getName());
        country.setVisible(request.getVisible());

        return mapToDto(countryRepository.save(country));
    }

    public void deleteCountry(Integer id) {
        if (!countryRepository.existsById(id)) {
            throw new IllegalArgumentException("Country not found.");
        }
        try {
            countryRepository.deleteById(id);
        } catch (Exception e) {
            // Catches Foreign Key Constraint Violations
            throw new IllegalArgumentException("Cannot delete this country because it is currently assigned to existing users. Consider updating it to 'Visible = false' instead.");
        }
    }

    private CountryDto mapToDto(Country country) {
        CountryDto dto = new CountryDto();
        dto.setId(country.getId());
        dto.setName(country.getName());
        dto.setVisible(country.isVisible());
        return dto;
    }
}