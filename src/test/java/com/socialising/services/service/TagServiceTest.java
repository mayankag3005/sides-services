package com.socialising.services.service;

import com.socialising.services.model.Tag;
import com.socialising.services.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
class TagServiceTest {
    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Long testTagId;
    private String testTagName;
    private Tag testTag;
    private Tag secondTestTag;

    @BeforeEach
    public void setUp() throws Exception {

        testTagId = 1L;
        testTagName = "test-tag";

        testTag = Tag.builder()
                .tagId(testTagId)
                .tag(testTagName)
                .build();

        secondTestTag = Tag.builder()
                .tagId(3L)
                .tag("second-test-tag")
                .build();
    }

    // addTag

    @Test
    public void should_not_add_tag_when_already_exists() {
        // Given
        Tag existingTag = Tag.builder()
                .tagId(2L)
                .tag(testTagName)
                .build();

        // Mock
        when(tagRepository.findByTagName(testTagName)).thenReturn(existingTag);

        // When
        Tag responseTag = tagService.addTag(testTag);

        // Then
        assertNull(responseTag);
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    public void should_add_tag_when_not_already_exists() {
        // Mock
        when(tagRepository.findByTagName(testTagName)).thenReturn(null);

        // When
        Tag responseTag = tagService.addTag(testTag);

        // Then
        assertNotNull(responseTag);
        assertEquals(testTagName, responseTag.getTag());
        verify(tagRepository, times(1)).save(testTag);
    }

    @Test
    public void should_add_tag_when_not_already_exists_and_should_be_lowercase() {
        // Given
        String testTagNameWithCase = "SoccER";
        Tag testTagWithCase = Tag.builder()
                .tagId(10L)
                .tag(testTagNameWithCase)
                .build();

        // Mock
        when(tagRepository.findByTagName(testTagNameWithCase)).thenReturn(null);

        // When
        Tag responseTag = tagService.addTag(testTagWithCase);

        // Then
        assertNotNull(responseTag);
        assertNotEquals(testTagNameWithCase, responseTag.getTag());
        assertEquals("soccer", responseTag.getTag());
        verify(tagRepository, times(1)).save(testTagWithCase);
    }

    @Test
    public void test_add_tag_with_exception() {
        // Mock
        when(tagRepository.findByTagName(testTagName)).thenReturn(null);
        doThrow(new RuntimeException("Exception occurred")).when(tagRepository).save(any(Tag.class));

        // When
        Tag responseTag = tagService.addTag(testTag);

        // Then
        assertNull(responseTag);
        verify(tagRepository, times(1)).save(testTag);
    }

    // getAllTags

    @Test
    public void should_get_all_tags() {
        // Given
        List<Tag> tagList = new ArrayList<>();
        tagList.add(testTag);
        tagList.add(secondTestTag);

        // Mock
        when(tagRepository.count()).thenReturn(2L);
        when(tagRepository.findAll()).thenReturn(tagList);

        // When
        ArrayList<Tag> result = tagService.getAllTags();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    public void should_get_zero_tags() {
        // Mock
        when(tagRepository.count()).thenReturn(0L);
        when(tagRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        ArrayList<Tag> result = tagService.getAllTags();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(tagRepository, times(1)).findAll();
    }

    // deleteTagById

    @Test
    public void should_not_delete_tag_when_not_exists() {
        // Mock
        when(tagRepository.findById(testTagId)).thenReturn(Optional.empty());

        // When
        int result = tagService.deleteTagById(testTagId);

        // Then
        assertEquals(-1, result);
        verify(tagRepository, never()).deleteById(testTagId);
    }

    @Test
    public void should_delete_tag_when_exists() {
        // Mock
        when(tagRepository.findById(testTagId)).thenReturn(Optional.of(testTag));

        // When
        int result = tagService.deleteTagById(testTagId);

        // Then
        assertEquals(1, result);
        verify(tagRepository, times(1)).deleteById(testTagId);
    }

    // deleteTagByName

    @Test
    public void should_not_delete_tag_with_name_when_not_exists() {
        // Given
        String tagNameToTest = "tag-name-to-test";
        // Mock
        when(tagRepository.findByTagName(tagNameToTest)).thenReturn(null);

        // When
        int result = tagService.deleteTagByName(tagNameToTest);

        // Then
        assertEquals(-1, result);
        verify(tagRepository, never()).deleteTagByName(tagNameToTest);
    }

    @Test
    public void should_delete_tag_with_name_when_exists() {
        // Mock
        when(tagRepository.findByTagName(testTagName)).thenReturn(testTag);

        // When
        int result = tagService.deleteTagByName(testTagName);

        // Then
        assertEquals(1, result);
        verify(tagRepository, times(1)).deleteTagByName(testTagName);
    }

    @Test
    public void should_delete_tag_with_name_when_exists_with_case() {
        // Given
        String testTagNameWithCase = "SoccER";
        Tag testTagWithCase = Tag.builder()
                .tagId(10L)
                .tag("soccer")
                .build();

        // Mock
        when(tagRepository.findByTagName("soccer")).thenReturn(testTagWithCase);

        // When
        int result = tagService.deleteTagByName(testTagNameWithCase);

        // Then
        assertEquals(1, result);
        verify(tagRepository, times(1)).deleteTagByName("soccer");
    }
}