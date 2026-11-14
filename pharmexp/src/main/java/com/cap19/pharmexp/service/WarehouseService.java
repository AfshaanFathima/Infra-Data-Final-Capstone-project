package com.cap19.pharmexp.service;

import com.cap19.pharmexp.dto.WarehouseRequest;
import com.cap19.pharmexp.dto.WarehouseResponse;
import com.cap19.pharmexp.entity.Warehouse;
import com.cap19.pharmexp.repository.WarehouseRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public WarehouseResponse getWarehouseById(Long id) {
        Warehouse warehouse = findWarehouse(id);
        return convertToResponse(warehouse);
    }

    public WarehouseResponse createWarehouse(WarehouseRequest request) {

        if (warehouseRepository.existsByWarehouseCode(request.getWarehouseCode())) {
            throw new IllegalArgumentException("Warehouse code already exists");
        }

        Warehouse warehouse = new Warehouse();
        copyRequestToEntity(request, warehouse);
        warehouse.setIsActive(true);

        return convertToResponse(warehouseRepository.save(warehouse));
    }

    public WarehouseResponse updateWarehouse(Long id, WarehouseRequest request) {

        Warehouse warehouse = findWarehouse(id);

        warehouseRepository.findByWarehouseCode(request.getWarehouseCode())
                .filter(existing -> !existing.getWarehouseId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Warehouse code already exists");
                });

        copyRequestToEntity(request, warehouse);

        return convertToResponse(warehouseRepository.save(warehouse));
    }

    public void deactivateWarehouse(Long id) {

        Warehouse warehouse = findWarehouse(id);
        warehouse.setIsActive(false);

        warehouseRepository.save(warehouse);
    }

    private Warehouse findWarehouse(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Warehouse not found: " + id));
    }

    private void copyRequestToEntity(
            WarehouseRequest request, Warehouse warehouse) {

        warehouse.setWarehouseCode(request.getWarehouseCode());
        warehouse.setWarehouseName(request.getWarehouseName());
        warehouse.setAddress(request.getAddress());
        warehouse.setCity(request.getCity());
        warehouse.setState(request.getState());
        warehouse.setPostalCode(request.getPostalCode());
        warehouse.setContactPerson(request.getContactPerson());
        warehouse.setContactPhone(request.getContactPhone());
    }

    private WarehouseResponse convertToResponse(Warehouse warehouse) {

        WarehouseResponse response = new WarehouseResponse();

        response.setWarehouseId(warehouse.getWarehouseId());
        response.setWarehouseCode(warehouse.getWarehouseCode());
        response.setWarehouseName(warehouse.getWarehouseName());
        response.setAddress(warehouse.getAddress());
        response.setCity(warehouse.getCity());
        response.setState(warehouse.getState());
        response.setPostalCode(warehouse.getPostalCode());
        response.setContactPerson(warehouse.getContactPerson());
        response.setContactPhone(warehouse.getContactPhone());
        response.setIsActive(warehouse.getIsActive());

        return response;
    }
}