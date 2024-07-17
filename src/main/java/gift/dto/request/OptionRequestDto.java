package gift.dto.request;

import jakarta.validation.constraints.*;

public record OptionRequestDto(

        @Pattern(
                regexp = "^[a-zA-Z0-9\\s()\\[\\]+\\-&/_]{1,50}$",
                message = "옵션 이름이 잘못 되었습니다."
        )
        String optionName,

        @NotNull(message = "수량을 입력하세요 (1 이상 1억 미만)")
        @Min(1)
        @Max(99999999)
        int optionQuantity
) { }
