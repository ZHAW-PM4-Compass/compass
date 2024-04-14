package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.exception.DayAlreadyExistsException;
import ch.zhaw.pm4.compass.backend.exception.DayNotFoundException;
import ch.zhaw.pm4.compass.backend.model.DaySheet;
import ch.zhaw.pm4.compass.backend.model.dto.CreateDaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.GetDaySheetDto;
import ch.zhaw.pm4.compass.backend.model.dto.UpdateDaySheetDto;
import ch.zhaw.pm4.compass.backend.repository.DaySheetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DaySheetService {

    @Autowired
    private DaySheetRepository daySheetRepository;

    public GetDaySheetDto createDay(CreateDaySheetDto createDay) {
        DaySheet daySheet = convertCreateDayDtoToDay(createDay);
        if(daySheetRepository.getDaySheetByDate(daySheet.getDate()).isPresent())
            throw new DayAlreadyExistsException(daySheet);
        return convertDayToGetDayDto(daySheetRepository.save(daySheet));
    }

    public GetDaySheetDto getDaySheetById(Long id) throws DayNotFoundException {
        return convertDayToGetDayDto(daySheetRepository.getDaySheetById(id).orElseThrow(() -> new DayNotFoundException(id)));
    }
    public GetDaySheetDto getDaySheetByDate(Date date) throws DayNotFoundException {
        return convertDayToGetDayDto(daySheetRepository.getDaySheetByDate(date).orElseThrow(() -> new DayNotFoundException(date)));
    }

    public List<GetDaySheetDto> getAllDaySheet() throws DayNotFoundException {
        List<DaySheet> daySheetList = daySheetRepository.findAll();
        return daySheetList.stream().map(daySheet -> convertDayToGetDayDto(daySheet)).toList();
    }

    public GetDaySheetDto updateDay(UpdateDaySheetDto updateDay) throws DayNotFoundException{
        DaySheet daySheet = daySheetRepository.getDaySheetById(updateDay.getId()).orElseThrow(() -> new DayNotFoundException(updateDay.getId()));
        daySheet.setDay_report(updateDay.getDay_report());
        daySheet.setDate(updateDay.getDate());
        return convertDayToGetDayDto(daySheetRepository.save(daySheet));
    }
    private DaySheet convertCreateDayDtoToDay(CreateDaySheetDto dayDto)
    {
        return new DaySheet(dayDto.getDay_report(),dayDto.getDate());
    }
    private GetDaySheetDto convertDayToGetDayDto(DaySheet daySheet)
    {
        return new GetDaySheetDto(daySheet.getId(), daySheet.getDay_report(), daySheet.getDate(), daySheet.getConfirmed(), daySheet.getTimestamps());
    }
}
