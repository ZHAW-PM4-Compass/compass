package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.dto.DaySheetDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DaySheetService {

    @Autowired
    private DaySheetRepository daySheetRepository;

    public DaySheetDto createDay(DaySheetDto createDay, String user_id)  {
        DaySheet daySheet = convertDaySheetDtoToDaySheet(createDay);
        daySheet.setUser_id(user_id);
        Optional<List<DaySheet>> optional = daySheetRepository.getDaySheetByDate(daySheet.getDate());
        if(optional.isPresent()) {
            List<DaySheet> list = optional.get();
            boolean  found = false;
            for(DaySheet day : list)
            {
                if (day.getUser_id().equals(user_id)) {
                    found = true;
                    break;
                }
            }
            if(found)
                return null;
        }
        return convertDaySheetToDaySheetDto(daySheetRepository.save(daySheet));
    }

    public DaySheetDto getDaySheetById(Long id, String user_id)  {
        Optional<DaySheet> optional = daySheetRepository.getDaySheetById(id);
        if(optional.isPresent())
            if(optional.get().getUser_id().equals(user_id))
                return convertDaySheetToDaySheetDto(optional.get());

        return null;
    }
    public DaySheetDto getDaySheetByDate(LocalDate date, String user_id)  {
        Optional<List<DaySheet>> optional = daySheetRepository.getDaySheetByDate(date);
        if(optional.isPresent())
        {
            List<DaySheet> list = optional.get();
            for(DaySheet day : list) {
                if (day.getUser_id().equals(user_id))
                    return convertDaySheetToDaySheetDto(day);
            }
        }

        return null;
    }

    public DaySheetDto updateDay(DaySheetDto updateDay, String user_id) {
        Optional<DaySheet> optional = daySheetRepository.getDaySheetById(updateDay.getId());
        if(optional.isEmpty())
            return null;
        DaySheet daySheet = optional.get();
        if(daySheet.getUser_id().equals(user_id)) {
            daySheet.setDay_report(updateDay.getDay_report());
            daySheet.setDate(updateDay.getDate());
            return convertDaySheetToDaySheetDto(daySheetRepository.save(daySheet));
        }
        return null;
    }
    public DaySheet convertDaySheetDtoToDaySheet(DaySheetDto dayDto)
    {
        return new DaySheet(dayDto.getId(), dayDto.getDay_report(), dayDto.getDate());
    }
    public DaySheetDto convertDaySheetToDaySheetDto(DaySheet daySheet)
    {
        return new DaySheetDto(daySheet.getId(), daySheet.getDay_report(), daySheet.getDate(), daySheet.getConfirmed(), daySheet.getTimestamps());
    }
}
