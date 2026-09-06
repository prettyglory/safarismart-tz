package tz.ac.dit.safarismart.controller;

import tz.ac.dit.safarismart.dto.AttractionSummaryDto;
import tz.ac.dit.safarismart.dto.DestinationSummaryDto;
import tz.ac.dit.safarismart.service.DestinationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/destinations")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @GetMapping
    public List<DestinationSummaryDto> getAll() {
        return destinationService.findAll();
    }

    @GetMapping("/{id}")
    public DestinationSummaryDto getOne(@PathVariable Long id) {
        return destinationService.findById(id);
    }

    @GetMapping("/{id}/attractions")
    public List<AttractionSummaryDto> getAttractions(@PathVariable Long id) {
        return destinationService.findAttractions(id);
    }
}
