package com.fitness.purchaseservice.service;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.PurchaseCategory;
import com.fitness.purchaseservice.model.PurchaseSubCategory;
import com.fitness.purchaseservice.repository.PurchaseCategoryRepository;
import com.fitness.purchaseservice.repository.PurchaseSubCategoryRepository;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PurchaseCategoryService {

    private final PurchaseCategoryRepository purchaseCategoryRepository;
    private final PurchaseSubCategoryRepository purchaseSubCategoryRepository;
    private final ClientPurchaseService clientPurchaseService;

    public List<PurchaseCategory> getAllPurchases() {
        return this.purchaseCategoryRepository.findAll();
    }

    public PurchaseCategory getPurchaseCategoryById(Integer id) {
        return this.purchaseCategoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Purchase category not found!"));
    }

    public List<PurchaseSubCategory> getSubCategoryByIds(List<Integer> ids) {
        return this.purchaseSubCategoryRepository.findAllById(ids);
    }

    public PurchaseCategory savePurchaseCategory(PurchaseCategory purchaseCategory) {
        return this.purchaseCategoryRepository.save(purchaseCategory);
    }

    public PurchaseSubCategory savePurchaseSubCategory(PurchaseSubCategory purchaseSubCategory) {
        return this.purchaseSubCategoryRepository.save(purchaseSubCategory);
    }

    public boolean purchaseSubCategoryExists(Integer id) {
        return this.purchaseSubCategoryRepository.existsById(id);
    }

    public void deletePurchaseCategory(PurchaseCategory purchaseCategory) {
        List<PurchaseSubCategory> purchaseSubCategories = this.purchaseSubCategoryRepository.findAllByPurchaseCategory(purchaseCategory);
        for (PurchaseSubCategory purchaseSubCategory : purchaseSubCategories) {
            List<ClientPurchase> clientPurchases =
                    this.clientPurchaseService.getAllPurchasesForClientByPurchaseSubCategory(purchaseSubCategory);
            if (clientPurchases.size() > 0)
                throw new BadRequestException("Cannot delete the category as it has the associated purchase!");
        }
        this.purchaseCategoryRepository.delete(purchaseCategory);
    }

    public void deletePurchaseSubCategory(Integer id) {
        this.purchaseSubCategoryRepository.deleteById(id);
    }
}
