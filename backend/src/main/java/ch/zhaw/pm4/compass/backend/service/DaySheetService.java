package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.Timestamp;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.TimestampDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DaySheetService {

	@Autowired
	private DaySheetRepository daySheetRepository;

    @Autowired
    TimestampService timestampService;
    public DaySheetDto createDay(DaySheetDto createDay, String user_id)  {
        DaySheet daySheet = convertDaySheetDtoToDaySheet(createDay);
        daySheet.setUserId(user_id);
        Optional<DaySheet> optional = daySheetRepository.findByDateAndUserId(daySheet.getDate(),user_id);
        if(optional.isPresent())
            return null;
        return convertDaySheetToDaySheetDto(daySheetRepository.save(daySheet));
    }

    public DaySheetDto getDaySheetById(Long id, String user_id)  {
        Optional<DaySheet> optional = daySheetRepository.findByIdAndUserId(id,user_id);
        if(optional.isPresent())
            return convertDaySheetToDaySheetDto(optional.get());

        return null;
    }
    public DaySheetDto getDaySheetByDate(LocalDate date, String user_id)  {
        Optional<DaySheet> optional = daySheetRepository.findByDateAndUserId(date,user_id);
        if(optional.isPresent())
            return convertDaySheetToDaySheetDto(optional.get());
        return null;
    }

    public DaySheetDto updateDay(DaySheetDto updateDay, String user_id) {
        Optional<DaySheet> optional = daySheetRepository.findByIdAndUserId(updateDay.getId(),user_id);
        if(optional.isEmpty())
            return null;
        DaySheet daySheet = optional.get();
        daySheet.setDayReport(updateDay.getDayReport());
        return convertDaySheetToDaySheetDto(daySheetRepository.save(daySheet));
    }
    public DaySheet convertDaySheetDtoToDaySheet(DaySheetDto dayDto)
    {
        return new DaySheet(dayDto.getId(), dayDto.getDayReport(), dayDto.getDate());
    }
    public DaySheetDto convertDaySheetToDaySheetDto(DaySheet daySheet)
    {
        List<TimestampDto> timestampDtos = new ArrayList<>();
        for(Timestamp timestamp : daySheet.getTimestamps())
            timestampDtos.add(timestampService.convertTimestampToTimestampDto(timestamp));
        return new DaySheetDto(daySheet.getId(), daySheet.getDayReport(), daySheet.getDate(), daySheet.getConfirmed(), timestampDtos);
    }
}
