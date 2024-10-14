package com.keshaparrot.gptorganizier.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.keshaparrot.gptorganizier.R;
import com.keshaparrot.gptorganizier.domain.Record;
import com.keshaparrot.gptorganizier.domain.TypeOfRecord;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class DatabaseServiceTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Context mockContext;
    @Mock
    private RecordDao mockRecordDao;

    private DatabaseService databaseService;
    private MutableLiveData<List<Record>> liveData;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockContext.getApplicationContext()).thenReturn(mockContext);
        AppDatabase mockDatabase = mock(AppDatabase.class);
        when(mockDatabase.recordDao()).thenReturn(mockRecordDao);
        AppDatabase.setInstance(mockDatabase);

        databaseService = DatabaseService.getInstance(mockContext);
        liveData = new MutableLiveData<>();
        when(mockRecordDao.getAll()).thenReturn(liveData);
    }

    @After
    public void tearDown() {
        databaseService = null;
    }

    @Test
    public void getUniqueRecords() {
        Record existingRecord1 = new Record(1L, "Header 1", "Content 1", "Description 1", new Date(1000), new Date(2000), TypeOfRecord.LINK);
        Record existingRecord2 = new Record(2L, "Header 2", "Content 2", "Description 2", new Date(1000), new Date(1000), TypeOfRecord.PROMPT);
        List<Record> localRecords = new ArrayList<>();
        localRecords.add(existingRecord1);
        localRecords.add(existingRecord2);

        Record newRecord1 = new Record(1L, "Header 1 Updated", "Content 1 Updated", "Description 1 Updated", new Date(1500), new Date(2500), TypeOfRecord.LINK);
        Record newRecord2 = new Record(3L, "Header 3", "Content 3", "Description 3", new Date(3000), new Date(3000), TypeOfRecord.PROMPT);
        List<Record> newRecords = new ArrayList<>();
        newRecords.add(newRecord1);
        newRecords.add(newRecord2);

        liveData.postValue(localRecords);

        List<Record> result = databaseService.getUniqueRecords(newRecords);

        assertEquals(3, result.size());
        assertTrue(result.contains(existingRecord1));
        assertTrue(result.contains(existingRecord2));
        assertTrue(result.contains(newRecord2));

        Record updatedRecord1 = result.stream().filter(record -> record.getId().equals(1L)).findFirst().orElse(null);
        assertNotNull(updatedRecord1);
        assertEquals(newRecord1, updatedRecord1);
    }

}
