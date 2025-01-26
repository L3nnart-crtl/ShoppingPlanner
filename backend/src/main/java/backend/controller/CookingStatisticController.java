package backend.controller;

import backend.model.CookingStatistic.CookingStatistic;
import backend.service.CookingStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * REST controller for handling requests related to cooking statistics.
 * <p>
 * Provides endpoints to retrieve statistics based on a specified date range.
 * </p>
 */
@RestController
@RequestMapping("/api/statistics")
public class CookingStatisticController {

    @Autowired
    private CookingStatisticService cookingStatisticService;

    /**
     * Retrieves cooking statistics for a specified date range.
     * <p>
     * This endpoint calculates and returns statistics for cooking activities
     * between the given start and end dates.
     * </p>
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate   the end date of the range (inclusive)
     * @return a {@link CookingStatistic} object containing the calculated statistics
     */
    @GetMapping
    public CookingStatistic getCookingStatistics(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return cookingStatisticService.calculateStatisticsForDateRange(startDate, endDate);
    }
}
