package gift.controller;

import gift.dto.request.OptionRequestDto;
import gift.dto.request.ProductRequestDto;
import gift.dto.response.CategoryResponseDto;
import gift.dto.response.OptionResponseDto;
import gift.dto.response.ProductResponseDto;
import gift.filter.AuthFilter;
import gift.filter.LoginFilter;
import gift.repository.token.TokenRepository;
import gift.service.CategoryService;
import gift.service.OptionService;
import gift.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    MockMvc mvc;

    @MockBean
    ProductService productService;

    @MockBean
    CategoryService categoryService;

    @MockBean
    OptionService optionService;

    @MockBean
    TokenRepository tokenRepository;

    @Test
    @DisplayName("필터 통과 실패 테스트")
    void 필터_통과_실패_테스트() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter(new AuthFilter(tokenRepository))
                .addFilter(new LoginFilter(tokenRepository))
                .build();

        mockMvc.perform(get("/products"))
                .andExpect(redirectedUrl("/home"))
                .andExpect(status().is3xxRedirection())
                .andDo(print());
    }

    @Test
    @DisplayName("상품 조회 페이징 API 테스트")
    void 상품_전체_조회_API_TEST() throws Exception {
        //given
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto(1L, "상품권","#00000");
        ProductResponseDto productDto1 = ProductResponseDto.of(1L,"test1", 1000, "abc.png", categoryResponseDto);
        ProductResponseDto productDto2 = ProductResponseDto.of(2L,"test2", 1000, "abc.png", categoryResponseDto);
        ProductResponseDto productDto3 = ProductResponseDto.of(3L,"test3", 1000, "abc.png", categoryResponseDto);
        ProductResponseDto productDto4 = ProductResponseDto.of(4L,"test4", 1000, "abc.png", categoryResponseDto);
        ProductResponseDto productDto5 = ProductResponseDto.of(5L,"test5", 1000, "abc.png", categoryResponseDto);

        List<ProductResponseDto> productDtos = new ArrayList<>(Arrays.asList(productDto1, productDto2, productDto3, productDto4, productDto5));

        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));

        //when
        given(productService.findProducts(pageRequest)).willReturn(productDtos);

        //then
        mvc.perform(get("/products"))
                .andExpect(view().name("manager"))
                .andExpect(model().attribute("productDtos",productDtos))
                .andDo(print());

    }

    @Test
    @DisplayName("상품 저장 GET API 테스트")
    void 상품_저장_GET_API_TEST() throws Exception {
        //given
        CategoryResponseDto categoryResponseDto1 = new CategoryResponseDto(1L, "상품권", "#0000");
        CategoryResponseDto categoryResponseDto2 = new CategoryResponseDto(2L, "고기", "#0000");
        CategoryResponseDto categoryResponseDto3 = new CategoryResponseDto(3L, "생선", "#0000");

        List<CategoryResponseDto> categoryResponseDtos = Arrays.asList(categoryResponseDto1, categoryResponseDto2, categoryResponseDto3);

        given(categoryService.findAllCategories()).willReturn(categoryResponseDtos);

        //when

        //then
        mvc.perform(get("/products/new"))
                .andExpect(view().name("addForm"))
                .andExpect(model().attribute("categories",categoryResponseDtos))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 저장 POST API 테스트")
    void 상품_저장_POST_API_TEST() throws Exception {
        //given

        //expected
        mvc.perform(post("/products/new")
                        .param("name","test1")
                        .param("price","1000")
                        .param("imageUrl","abc.png")
                        .param("categoryId", "1")
                        .param("optionName", "Test")
                        .param("optionQuantity", "100")
                )
                .andExpect(view().name("redirect:/products"))
                .andExpect(redirectedUrl("/products"))
                .andDo(print());

        verify(productService, times(1)).addProduct(any(ProductRequestDto.class), any(OptionRequestDto.class));
    }

    @Test
    @DisplayName("상품 수정 GET API 테스트")
    void 상품_수정_GET_API_TEST() throws Exception {
        //given
        CategoryResponseDto categoryResponseDto1 = new CategoryResponseDto(1L, "상품권", "#0000");
        CategoryResponseDto categoryResponseDto2 = new CategoryResponseDto(2L, "고기", "#0000");
        CategoryResponseDto categoryResponseDto3 = new CategoryResponseDto(3L, "생선", "#0000");
        List<CategoryResponseDto> categoryResponseDtos = Arrays.asList(categoryResponseDto1, categoryResponseDto2, categoryResponseDto3);

        ProductResponseDto productResponseDto = ProductResponseDto.of(1L, "test", 1000, "abc.png", categoryResponseDto1);

        //when
        given(productService.findProductById(1L)).willReturn(productResponseDto);
        given(categoryService.findAllCategories()).willReturn(categoryResponseDtos);

        //then
        mvc.perform(get("/products/{id}/edit", 1L))
                .andExpect(view().name("editForm"))
                .andExpect(model().attribute("productDto",productResponseDto))
                .andExpect(model().attribute("categories",categoryResponseDtos))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 수정 POST API 테스트")
    void 상품_수정_POST_API_TEST() throws Exception {
        //given

        //when

        //then
        mvc.perform(post("/products/{id}/edit", 1L)
                        .param("price","2000")
                        .param("categoryId", "1"))
                .andExpect(view().name("redirect:/products"))
                .andExpect(redirectedUrl("/products"))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 삭제 POST API 테스트")
    void 상품_삭제_POST_API_TEST() throws Exception {
        //given

        //when

        //then
        mvc.perform(post("/products/{id}/delete", 1L))
                .andExpect(view().name("redirect:/products"))
                .andExpect(redirectedUrl("/products"))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 옵션 조회 API 테스트")
    void 상품_옵션_조회_API_테스트() throws Exception {
        //given
        OptionResponseDto optionResponseDto1 = new OptionResponseDto(1L, "TEST1", 1);
        OptionResponseDto optionResponseDto2 = new OptionResponseDto(2L, "TEST2", 2);
        OptionResponseDto optionResponseDto3 = new OptionResponseDto(3L, "TEST3", 3);

        List<OptionResponseDto> optionDtos = Arrays.asList(optionResponseDto1, optionResponseDto2, optionResponseDto3);

        given(optionService.findOptionsByProduct(1L)).willReturn(optionDtos);

        //excepted
        mvc.perform(get("/products/{id}/options", 1L))
                .andExpect(view().name("option/optionForm"))
                .andExpect(model().attribute("productId",1L))
                .andExpect(model().attribute("options",optionDtos))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 옵션 저장 API 테스트")
    void 상품_옵션_저장_API_테스트() throws Exception {
        //given

        //excepted
        mvc.perform(post("/products/{id}/options", 1L)
                        .param("optionName", "TEST")
                        .param("optionQuantity", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/products/{id}/options"))
                .andExpect(redirectedUrl("/products/1/options"))
                .andDo(print());
    }

    @Test
    @DisplayName("상품 옵션 저장 Form API 테스트")
    void 상품_옵션_저장_폼_API_테스트() throws Exception {
        //given

        //excepted
        mvc.perform(get("/products/new/{id}/options", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("option/addOptionForm"))
                .andExpect(model().attribute("productId",1L))
                .andDo(print());

    }

}