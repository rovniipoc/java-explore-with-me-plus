package ru.practicum.ewm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ViewStatsOutputDtoMapper {
    public static ViewStatsOutputDto MapToViewStatsOutputDto(ViewStats viewStats) {
        ViewStatsOutputDto viewStatsOutputDto = new ViewStatsOutputDto();
        viewStatsOutputDto.setApp(viewStats.getApp());
        viewStatsOutputDto.setUri(viewStats.getUri());
        viewStatsOutputDto.setHits(viewStats.getHits());
        return viewStatsOutputDto;
    }

    public static List<ViewStatsOutputDto> MapToViewStatsOutputDto(List<ViewStats> viewStatsList) {
        List<ViewStatsOutputDto> statsDtoList = new ArrayList<>();
        for (ViewStats viewStats : viewStatsList) {
            statsDtoList.add(MapToViewStatsOutputDto(viewStats));
        }
        statsDtoList.sort(Comparator.comparingLong(ViewStatsOutputDto::getHits).reversed());
        return statsDtoList;
    }
}
