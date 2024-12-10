package backend.controller;

import backend.model.CookingStatistic;
import backend.service.CookingStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class CookingStatisticController {

    @Autowired
    private CookingStatisticService cookingStatisticService;

    @GetMapping
    public CookingStatistic getCookingStatistics() {
        return cookingStatisticService.calculateStatistics();
    }
}
