package com.ems.actions;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ems.model.Category;
import com.ems.model.Event;
import com.ems.service.EventService;
import com.ems.util.ApplicationUtil;

public class EventSearchAction {
    private final EventService eventService;

    public EventSearchAction() {
        this.eventService = ApplicationUtil.eventService();
    }

    public List<Category> getAllCategories() {
        return eventService.getAllCategory();
    }

    public List<Event> searchByCategory(int categoryId) {
        return eventService.searchBycategory(categoryId);
    }

    public List<Event> searchByDate(LocalDate date) {
        return eventService.searchByDate(date);
    }

    public List<Event> searchByDateRange(LocalDate startDate, LocalDate endDate) {
        return eventService.searchByDateRange(startDate, endDate);
    }

    public Map<Integer, String> getAllCities() {
        return eventService.getAllCities();
    }

    public List<Event> searchByCity(int cityId) {
        return eventService.searchByCity(cityId);
    }

    public List<Event> filterByPrice(double minPrice, double maxPrice) {
        return eventService.filterByPrice(minPrice, maxPrice);
    }
}