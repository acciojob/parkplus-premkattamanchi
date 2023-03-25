package com.driver.services.impl;

import com.driver.model.ParkingLot;
import com.driver.model.Spot;
import com.driver.model.SpotType;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.SpotRepository;
import com.driver.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {
    @Autowired
    ParkingLotRepository parkingLotRepository1;
    @Autowired
    SpotRepository spotRepository1;
    @Override
    public ParkingLot addParkingLot(String name, String address) {
         ParkingLot parkingLot=new ParkingLot();
         parkingLot.setName(name);
         parkingLot.setAddress(address);
         parkingLotRepository1.save(parkingLot);
         return parkingLot;
    }

    @Override
    public Spot addSpot(int parkingLotId, Integer numberOfWheels, Integer pricePerHour) {
        ParkingLot parkingLot=parkingLotRepository1.findById(parkingLotId).get();
        Spot spot=new Spot();
        spot.setPricePerHour(pricePerHour);
        spot.setOccupied(false);
        spot.setParkingLot(parkingLot);
        if(numberOfWheels<=2)
            spot.setSpotType(SpotType.TWO_WHEELER);
        else if(numberOfWheels<=4)
            spot.setSpotType(SpotType.FOUR_WHEELER);
        else
            spot.setSpotType(SpotType.OTHERS);
        //adding in parkingLot
        parkingLot.getSpotList().add(spot);
        //saving parent entity
        parkingLotRepository1.save(parkingLot);
        return spot;
    }

    @Override
    public void deleteSpot(int spotId) {
        try{
            Spot spot=spotRepository1.findById(spotId).get();
            ParkingLot parkingLot=spot.getParkingLot();
            List<Spot> spotList=parkingLot.getSpotList();
            for(Spot sp:spotList){
                if(sp.getId()==spotId)
                    spotList.remove(sp);
            }
            parkingLot.setSpotList(spotList);
            parkingLotRepository1.save(parkingLot);
        }
        catch(Exception e){
            System.out.println("spot not present");
        }
    }

    @Override
    public Spot updateSpot(int parkingLotId, int spotId, int pricePerHour) {
         ParkingLot parkingLot=parkingLotRepository1.findById(parkingLotId).get();
         List<Spot> spotList=parkingLot.getSpotList();
         Spot updatedSpot=null;
         for(Spot spot:spotList){
             if(spot.getId()==spotId){
                 spot.setPricePerHour(pricePerHour);
                 updatedSpot=spot;
             }
         }

         parkingLot.setSpotList(spotList);
         parkingLotRepository1.save(parkingLot);
         return updatedSpot;
    }

    @Override
    public void deleteParkingLot(int parkingLotId) {
        try{
            ParkingLot parkingLot=parkingLotRepository1.findById(parkingLotId).get();
            List<Spot> spotList=parkingLot.getSpotList();
            for(Spot spot:spotList){
                spotRepository1.deleteById(spot.getId());
            }
            parkingLotRepository1.deleteById(parkingLotId);
        }
        catch(Exception e){

        }

    }
}
