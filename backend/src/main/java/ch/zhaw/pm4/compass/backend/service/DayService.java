package ch.zhaw.pm4.compass.backend.service;

import ch.zhaw.pm4.compass.backend.exception.DayAlreadyExistsException;
import ch.zhaw.pm4.compass.backend.exception.DayNotFoundException;
import ch.zhaw.pm4.compass.backend.model.Day;
import ch.zhaw.pm4.compass.backend.model.dto.CreateDayDto;
import ch.zhaw.pm4.compass.backend.model.dto.GetDayDto;
import ch.zhaw.pm4.compass.backend.model.dto.UpdateDayDto;
import ch.zhaw.pm4.compass.backend.repository.DayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DayService {

    @Autowired
    private DayRepository dayRepository;

    public GetDayDto createDay(CreateDayDto createDay) {
        Day day = convertCreateDayDtoToDay(createDay);
        if(dayRepository.getDayByDate(day.getDate()).isPresent())
            throw new DayAlreadyExistsException(day);
        return convertDayToGetDayDto(dayRepository.save(day));
    }

    public GetDayDto getDayById(Long id) throws DayNotFoundException {
        return convertDayToGetDayDto(dayRepository.getDayById(id).orElseThrow(() -> new DayNotFoundException(id)));
    }
    public GetDayDto getDayByDate(Date date) throws DayNotFoundException {
        return convertDayToGetDayDto(dayRepository.getDayByDate(date).orElseThrow(() -> new DayNotFoundException(date)));
    }

    public GetDayDto updateDay(UpdateDayDto updateDay) throws DayNotFoundException{
        Day day = dayRepository.getDayById(updateDay.getId()).orElseThrow(() -> new DayNotFoundException(updateDay.getId()));
        day.setDay_report(updateDay.getDay_report());
        return convertDayToGetDayDto(dayRepository.save(day));
    }
    private Day convertCreateDayDtoToDay(CreateDayDto dayDto)
    {
        return new Day(dayDto.getDay_report(),dayDto.getDate());
    }
    private GetDayDto convertDayToGetDayDto(Day day)
    {
        return new GetDayDto(day.getDay_report(),day.getDate(),day.getConfirmed(),day.getTimestamps());
    }
}
