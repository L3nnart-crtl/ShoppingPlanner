package backend.controller;

public class MealPlanResponse {

    private Long breackfastId;
    private Long lunchId;
    private Long dinnerId;


    public MealPlanResponse(final Long breackfastId, final Long lunchId, final Long dinnerId) {
        this.breackfastId = breackfastId;
        this.lunchId = lunchId;
        this.dinnerId = dinnerId;
    }

    public Long getBreackfastId() {
        return breackfastId;
    }

    public void setBreackfastId(final Long breackfastId) {
        this.breackfastId = breackfastId;
    }

    public Long getLunchId() {
        return lunchId;
    }

    public void setLunchId(final Long lunchId) {
        this.lunchId = lunchId;
    }

    public Long getDinnerId() {
        return dinnerId;
    }

    public void setDinnerId(final Long dinnerId) {
        this.dinnerId = dinnerId;
    }
}
