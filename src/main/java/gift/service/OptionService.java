package gift.service;

import gift.domain.Option;
import gift.domain.Product;
import gift.dto.request.OptionRequestDto;
import gift.dto.response.OptionResponseDto;
import gift.exception.EntityNotFoundException;
import gift.exception.NameDuplicationException;
import gift.repository.option.OptionRepository;
import gift.repository.product.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OptionService {

    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionService(OptionRepository optionRepository, ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OptionResponseDto saveOption(OptionRequestDto optionRequestDto, Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품은 존재하지 않습니다."));

        optionRepository.findOptionByNameAndProductId(optionRequestDto.optionName(), productId)
                .ifPresent(
                        e -> {
                            throw new NameDuplicationException("이미 존재하는 옵션 입니다.");
                        }
                );

        Option option = new Option(optionRequestDto.optionName(), optionRequestDto.optionQuantity());

        option.addProduct(product);

        Option savedOption = optionRepository.save(option);

        return OptionResponseDto.from(savedOption);
    }

    public List<OptionResponseDto> findOptionsByProduct(Long productId){
        return optionRepository.findOptionsByProductId(productId).stream()
                .map(OptionResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public OptionResponseDto deleteOneOption(Long optionId){
        Option option = optionRepository.findById(optionId).orElseThrow(() -> new EntityNotFoundException("해당 옵션은 존재하지 않습니다."));

        optionRepository.delete(option);

        return OptionResponseDto.from(option);
    }
}
