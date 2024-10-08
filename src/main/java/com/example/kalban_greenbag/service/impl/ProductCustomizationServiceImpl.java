package com.example.kalban_greenbag.service.impl;

import com.example.kalban_greenbag.constant.ConstError;
import com.example.kalban_greenbag.constant.ConstStatus;
import com.example.kalban_greenbag.dto.request.product_customization.AddProductCustomizationRequest;
import com.example.kalban_greenbag.dto.request.product_customization.UpdateProductCustomizationRequest;
import com.example.kalban_greenbag.dto.response.product_customization.ProductCustomizationResponse;
import com.example.kalban_greenbag.entity.CustomizationOption;
import com.example.kalban_greenbag.entity.Product;
import com.example.kalban_greenbag.entity.ProductCustomization;
import com.example.kalban_greenbag.enums.ErrorCode;
import com.example.kalban_greenbag.exception.BaseException;
import com.example.kalban_greenbag.model.PagingModel;
import com.example.kalban_greenbag.repository.CustomizationOptionRepository;
import com.example.kalban_greenbag.repository.ProductCustomizationRepository;
import com.example.kalban_greenbag.repository.ProductRepository;
import com.example.kalban_greenbag.service.IProductCustomizationService;
import com.example.kalban_greenbag.utils.SecurityUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductCustomizationServiceImpl implements IProductCustomizationService {

    @Autowired
    private ProductCustomizationRepository productCustomizationRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CustomizationOptionRepository customizationOptionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductCustomizationResponse findById(UUID id) throws BaseException {
        try {
            Optional<ProductCustomization> productCustomization = productCustomizationRepository.findById(id);
            if (productCustomization.isEmpty()) {
                throw new BaseException(ErrorCode.ERROR_404.getCode(), ConstError.ProductCustomization.PRODUCT_CUSTOMIZATION_NOT_FOUND, ErrorCode.ERROR_500.getMessage());
            }
            return modelMapper.map(productCustomization.get(), ProductCustomizationResponse.class);
        } catch (Exception exception) {
            if (exception instanceof BaseException) {
                throw exception;
            }
            throw new BaseException(ErrorCode.ERROR_500.getCode(), exception.getMessage(), ErrorCode.ERROR_500.getMessage());
        }
    }

    @Override
    public PagingModel<ProductCustomizationResponse> getAll(Integer page, Integer limit) throws BaseException {
        try {
            if (page == null || limit == null) {
                page = 1;
                limit = 10;
            }
            PagingModel<ProductCustomizationResponse> result = new PagingModel<>();
            result.setPage(page);
            Pageable pageable = PageRequest.of(page - 1, limit);

            List<ProductCustomization> productCustomizationList = productCustomizationRepository.findAllByOrderByCreatedDateDesc(pageable);
            List<ProductCustomizationResponse> productCustomizationResponses = productCustomizationList.stream()
                    .map(productCustomization -> modelMapper.map(productCustomization, ProductCustomizationResponse.class))
                    .toList();

            result.setListResult(productCustomizationResponses);
            result.setTotalPage((int) Math.ceil((double) totalItems() / limit));
            result.setLimit(limit);

            return result;
        } catch (Exception exception) {
            throw new BaseException(ErrorCode.ERROR_500.getCode(), exception.getMessage(), ErrorCode.ERROR_500.getMessage());
        }
    }

    private int totalItems() {
        return (int) productCustomizationRepository.count();
    }

    @Override
    public PagingModel<ProductCustomizationResponse> findAllByStatusTrue(Integer page, Integer limit) throws BaseException {
        try {
            if (page == null || limit == null) {
                page = 1;
                limit = 10;
            }
            PagingModel<ProductCustomizationResponse> result = new PagingModel<>();
            result.setPage(page);
            Pageable pageable = PageRequest.of(page - 1, limit);

            List<ProductCustomization> productCustomizationList = productCustomizationRepository.findAllByStatusOrderByCreatedDateDesc(ConstStatus.ACTIVE_STATUS, pageable);
            List<ProductCustomizationResponse> productCustomizationResponses = productCustomizationList.stream()
                    .map(productCustomization -> modelMapper.map(productCustomization, ProductCustomizationResponse.class))
                    .toList();

            result.setListResult(productCustomizationResponses);
            result.setTotalPage((int) Math.ceil((double) totalActiveItems() / limit));
            result.setLimit(limit);

            return result;
        } catch (Exception exception) {
            throw new BaseException(ErrorCode.ERROR_500.getCode(), exception.getMessage(), ErrorCode.ERROR_500.getMessage());
        }
    }

    private int totalActiveItems() {
        return (int) productCustomizationRepository.countByStatus(ConstStatus.ACTIVE_STATUS);
    }

    @Override
    public ProductCustomizationResponse create(AddProductCustomizationRequest addProductCustomizationRequest) throws BaseException {
        try {
            Product product = productRepository.findById(addProductCustomizationRequest.getProductId())
                    .orElseThrow(() -> new BaseException(ErrorCode.ERROR_404.getCode(),
                            ConstError.ProductCustomization.PRODUCT_CUSTOMIZATION_NOT_FOUND,
                            "The product with the provided ID does not exist."));

            CustomizationOption customizationOption = customizationOptionRepository.findById(addProductCustomizationRequest.getOptionId())
                    .orElseThrow(() -> new BaseException(ErrorCode.ERROR_404.getCode(),
                            ConstError.CustomizationOption.CUSTOMIZATION_OPTION_NOT_FOUND,
                            "The option with the provided ID does not exist."));

            ProductCustomization newProductCustomization = new ProductCustomization();
            newProductCustomization.setStatus(addProductCustomizationRequest.getStatus());
            newProductCustomization.setImageURL(addProductCustomizationRequest.getImageURL());
            newProductCustomization.setCreatedBy(SecurityUtil.getCurrentUsername());
            newProductCustomization.setProductID(product);
            newProductCustomization.setOptionID(customizationOption);
            newProductCustomization.setUserId(addProductCustomizationRequest.getUserId());
            newProductCustomization.setCustomValue(addProductCustomizationRequest.getCustomValue());
            newProductCustomization.setTotalPrice(
                    addProductCustomizationRequest.getTotalPrice().add(product.getFinalPrice())
            );
            newProductCustomization.setReason(null);
            ProductCustomization savedProductCustomization = productCustomizationRepository.save(newProductCustomization);

            return modelMapper.map(savedProductCustomization, ProductCustomizationResponse.class);
        } catch (Exception exception) {
            throw new BaseException(ErrorCode.ERROR_500.getCode(), exception.getMessage(), ErrorCode.ERROR_500.getMessage());
        }
    }

    @Override
    public ProductCustomizationResponse update(UpdateProductCustomizationRequest updateProductCustomizationRequest) throws BaseException {
        try {
            // Tìm ProductCustomization dựa trên ID từ request
            ProductCustomization productCustomization = productCustomizationRepository.findById(updateProductCustomizationRequest.getId())
                    .orElseThrow(() -> new BaseException(ErrorCode.ERROR_404.getCode(),
                            ConstError.ProductCustomization.PRODUCT_CUSTOMIZATION_NOT_FOUND,
                            ErrorCode.ERROR_404.getMessage()));

            // Cập nhật status (nếu có)
            if (updateProductCustomizationRequest.getStatus() != null) {
                productCustomization.setStatus(updateProductCustomizationRequest.getStatus());
            }

            // Cập nhật imageURL (nếu có)
            if (updateProductCustomizationRequest.getImageURL() != null) {
                productCustomization.setImageURL(updateProductCustomizationRequest.getImageURL());
            }

            // Cập nhật customValue (nếu có)
            if (updateProductCustomizationRequest.getCustomValue() != null) {
                productCustomization.setCustomValue(updateProductCustomizationRequest.getCustomValue());
            }

            // Cập nhật price (nếu có)
            if (updateProductCustomizationRequest.getTotalPrice() != null) {
                productCustomization.setTotalPrice(updateProductCustomizationRequest.getTotalPrice());
            }

            // Cập nhật productId (nếu có)
            if (updateProductCustomizationRequest.getProductId() != null) {
                Product product = productRepository.findById(updateProductCustomizationRequest.getProductId())
                        .orElseThrow(() -> new BaseException(ErrorCode.ERROR_404.getCode(),
                                "Product not found",
                                "The product with the provided ID does not exist."));
                productCustomization.setProductID(product); // Use setProduct instead of setProductID
            }

            // Cập nhật optionId (nếu có)
            if (updateProductCustomizationRequest.getOptionId() != null) {
                CustomizationOption customizationOption = customizationOptionRepository.findById(updateProductCustomizationRequest.getOptionId())
                        .orElseThrow(() -> new BaseException(ErrorCode.ERROR_404.getCode(),
                                "Customization Option not found",
                                "The option with the provided ID does not exist."));
                productCustomization.setOptionID(customizationOption); // Use setOption instead of setOptionID
            }
            if (updateProductCustomizationRequest.getReason() != null) {
                productCustomization.setReason(updateProductCustomizationRequest.getReason());
            }

            // Cập nhật thông tin người sửa đổi
            productCustomization.setModifiedBy(SecurityUtil.getCurrentUsername());

            // Lưu lại ProductCustomization đã được cập nhật
            ProductCustomization updatedProductCustomization = productCustomizationRepository.save(productCustomization);

            // Trả về response đã được map từ entity
            return modelMapper.map(updatedProductCustomization, ProductCustomizationResponse.class);

        } catch (Exception exception) {
            if (exception instanceof BaseException) {
                throw exception;
            }
            throw new BaseException(ErrorCode.ERROR_500.getCode(), exception.getMessage(), ErrorCode.ERROR_500.getMessage());
        }
    }


    @Override
    public Boolean changeStatus(UUID id) throws BaseException {
        try {
            ProductCustomization productCustomization = productCustomizationRepository.findById(id)
                    .orElseThrow(() -> new BaseException(ErrorCode.ERROR_404.getCode(),
                            ConstError.ProductCustomization.PRODUCT_CUSTOMIZATION_NOT_FOUND,
                            ErrorCode.ERROR_404.getMessage()));

            productCustomization.setStatus(ConstStatus.INACTIVE_STATUS);
            productCustomizationRepository.save(productCustomization);

            return true;
        } catch (Exception exception) {
            if (exception instanceof BaseException) {
                throw exception;
            }
            throw new BaseException(ErrorCode.ERROR_500.getCode(), exception.getMessage(), ErrorCode.ERROR_500.getMessage());
        }
    }

    @Override
    public PagingModel<ProductCustomizationResponse> getProductCustomByUserId(UUID userId, Integer page, Integer limit) throws BaseException {
        try {
            if (page == null || limit == null) {
                page = 1;
                limit = 10;
            }
            PagingModel<ProductCustomizationResponse> result = new PagingModel<>();
            result.setPage(page);
            Pageable pageable = PageRequest.of(page - 1, limit);

            List<ProductCustomization> productCustomizationList = productCustomizationRepository.findAllByUserIdOrderByCreatedDateDesc(userId, pageable);
            List<ProductCustomizationResponse> productCustomizationResponses = productCustomizationList.stream()
                    .map(productCustomization -> modelMapper.map(productCustomization, ProductCustomizationResponse.class))
                    .toList();

            result.setListResult(productCustomizationResponses);

            long totalItems = productCustomizationRepository.countByUserId(userId);
            result.setTotalPage((int) Math.ceil((double) totalItems / limit));
            result.setLimit(limit);

            return result;
        } catch (Exception exception) {
            throw new BaseException(ErrorCode.ERROR_500.getCode(), exception.getMessage(), ErrorCode.ERROR_500.getMessage());
        }
    }
}
