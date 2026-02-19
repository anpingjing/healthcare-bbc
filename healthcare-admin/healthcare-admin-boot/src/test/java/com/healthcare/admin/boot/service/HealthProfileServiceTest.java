package com.healthcare.admin.boot.service;

import com.healthcare.admin.boot.dto.HealthProfileDTO;
import com.healthcare.admin.boot.entity.HealthProfile;
import com.healthcare.admin.boot.mapper.HealthProfileMapper;
import com.healthcare.admin.boot.service.impl.HealthProfileServiceImpl;
import com.healthcare.admin.boot.vo.HealthProfileVO;
import com.healthcare.admin.common.exception.BusinessException;
import com.healthcare.admin.common.result.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HealthProfileServiceTest {

    @Mock
    private HealthProfileMapper healthProfileMapper;

    @InjectMocks
    private HealthProfileServiceImpl healthProfileService;

    private HealthProfile testProfile;
    private HealthProfileDTO profileDTO;

    @BeforeEach
    void setUp() {
        testProfile = new HealthProfile();
        testProfile.setProfileId(1L);
        testProfile.setUserId(1L);
        testProfile.setRealName("张三");
        testProfile.setGender(1);
        testProfile.setPhone("13800138000");
        testProfile.setBirthDate(LocalDate.of(1980, 5, 15));
        testProfile.setHeight(new BigDecimal("175"));
        testProfile.setWeight(new BigDecimal("70"));
        testProfile.setBmi(new BigDecimal("22.86"));
        testProfile.setHealthScore(85);
        testProfile.setHealthLevel("low");
        testProfile.setStatus(1);

        profileDTO = new HealthProfileDTO();
        profileDTO.setRealName("张三");
        profileDTO.setGender(1);
        profileDTO.setPhone("13800138000");
        profileDTO.setHeight(new BigDecimal("175"));
        profileDTO.setWeight(new BigDecimal("70"));
    }

    @Test
    void testGetProfileByIdSuccess() {
        when(healthProfileMapper.selectById(1L)).thenReturn(testProfile);

        HealthProfileVO result = healthProfileService.getProfileById(1L);

        assertNotNull(result);
        assertEquals("张三", result.getRealName());
        assertEquals("13800138000", result.getPhone());
    }

    @Test
    void testGetProfileByIdNotFound() {
        when(healthProfileMapper.selectById(1L)).thenReturn(null);

        assertThrows(BusinessException.class, () -> {
            healthProfileService.getProfileById(1L);
        });
    }

    @Test
    void testGetProfileByUserIdSuccess() {
        when(healthProfileMapper.selectByUserId(1L)).thenReturn(testProfile);

        HealthProfileVO result = healthProfileService.getProfileByUserId(1L);

        assertNotNull(result);
        assertEquals("张三", result.getRealName());
    }

    @Test
    void testCreateProfileSuccess() {
        when(healthProfileMapper.insert(any(HealthProfile.class))).thenAnswer(invocation -> {
            HealthProfile profile = invocation.getArgument(0);
            profile.setProfileId(1L);
            return 1;
        });

        HealthProfileVO result = healthProfileService.createProfile(profileDTO);

        assertNotNull(result);
        assertEquals("张三", result.getRealName());
        assertNotNull(result.getBmi());
    }

    @Test
    void testUpdateProfileSuccess() {
        when(healthProfileMapper.selectById(1L)).thenReturn(testProfile);

        profileDTO.setRealName("李四");
        HealthProfileVO result = healthProfileService.updateProfile(1L, profileDTO);

        assertNotNull(result);
        verify(healthProfileMapper).updateById(any(HealthProfile.class));
    }

    @Test
    void testDeleteProfileSuccess() {
        when(healthProfileMapper.selectById(1L)).thenReturn(testProfile);

        healthProfileService.deleteProfile(1L);

        verify(healthProfileMapper).deleteById(1L);
    }

    @Test
    void testCalculateHealthScore() {
        testProfile.setBmi(new BigDecimal("22.86"));
        testProfile.setChronicDiseases(null);
        testProfile.setAllergies(null);
        testProfile.setFamilyHistory(null);
        when(healthProfileMapper.selectById(1L)).thenReturn(testProfile);

        Integer score = healthProfileService.calculateHealthScore(1L);

        assertNotNull(score);
        assertTrue(score >= 0 && score <= 100);
    }

    @Test
    void testAssignServiceRoles() {
        when(healthProfileMapper.selectById(1L)).thenReturn(testProfile);

        healthProfileService.assignServiceRoles(1L, 1L, 2L, 3L, 4L);

        verify(healthProfileMapper).updateById(any(HealthProfile.class));
    }

    @Test
    void testGetHealthStatistics() {
        when(healthProfileMapper.selectCount(any())).thenReturn(100L);
        
        Map<String, Object> levelCount = new HashMap<>();
        levelCount.put("health_level", "low");
        levelCount.put("count", 50L);
        when(healthProfileMapper.countByHealthLevel()).thenReturn(Arrays.asList(levelCount));

        Map<String, Object> statistics = healthProfileService.getHealthStatistics();

        assertNotNull(statistics);
        assertEquals(100L, statistics.get("total"));
        assertNotNull(statistics.get("levelDistribution"));
    }
}
