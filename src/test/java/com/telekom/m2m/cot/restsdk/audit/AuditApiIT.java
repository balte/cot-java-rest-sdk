package com.telekom.m2m.cot.restsdk.audit;

import com.telekom.m2m.cot.restsdk.CloudOfThingsPlatform;
import com.telekom.m2m.cot.restsdk.inventory.ManagedObject;
import com.telekom.m2m.cot.restsdk.util.TestHelper;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Date;

/**
 * Created by Andreas Dyck on 24.07.17.
 */
public class AuditApiIT {

    CloudOfThingsPlatform cotPlat = new CloudOfThingsPlatform(TestHelper.TEST_HOST, TestHelper.TEST_USERNAME, TestHelper.TEST_PASSWORD);
    private ManagedObject testManagedObject;

    @BeforeClass
    public void setUp() {
        testManagedObject = TestHelper.createRandomManagedObjectInPlatform(cotPlat, "fake_name");
    }

    @AfterClass
    public void tearDown() {
        TestHelper.deleteManagedObjectInPlatform(cotPlat, testManagedObject);
    }


    @Test
    public void testAuditApi() throws Exception {
        // given
        final String text = "new audit record created";
        final String type = "com_telekom_audit_TestType";
        final Date timeOfAuditRecording = new Date();
        // reduce time by 1 second to get a difference between "time" set by client and "creationTime" set by platform
        // because sometimes there is a difference between client time and platform time
        timeOfAuditRecording.setTime(timeOfAuditRecording.getTime()-1000);
        final String user = "integration-tester";
        final String application = this.getClass().getSimpleName();
        final String activity = "Create Audit Record";

        final AuditRecord auditRecord = new AuditRecord();
        auditRecord.setText(text);
        auditRecord.setType(type);
        auditRecord.setTime(timeOfAuditRecording);
        auditRecord.setSource(testManagedObject);
        auditRecord.setUser(user);
        auditRecord.setApplication(application);
        auditRecord.setActivity(activity);

        final AuditApi auditApi = cotPlat.getAuditApi();

        // when
        final AuditRecord createdAuditRecord = auditApi.createAuditRecord(auditRecord);

        // then
        Assert.assertNotNull(auditRecord.getId(), "Should now have an Id!");
        Assert.assertEquals(auditRecord.getId(), createdAuditRecord.getId());

        // when
        final AuditRecord retrievedAuditRecord = auditApi.getAuditRecord(createdAuditRecord.getId());

        // then
        Assert.assertEquals(retrievedAuditRecord.getId(), createdAuditRecord.getId());
        Assert.assertEquals(retrievedAuditRecord.getText(), text);
        Assert.assertEquals(retrievedAuditRecord.getType(), type);
        Assert.assertEquals(retrievedAuditRecord.getTime().compareTo(timeOfAuditRecording), 0);
        // TODO: resolve java.lang.ClassCastException: com.google.gson.JsonObject cannot be cast to com.telekom.m2m.cot.restsdk.util.ExtensibleObject
//        Assert.assertEquals(retrievedAuditRecord.getSource().getId(), testManagedObject.getId());
        Assert.assertEquals(retrievedAuditRecord.getUser(), user);
        Assert.assertEquals(retrievedAuditRecord.getApplication(), application);
        Assert.assertEquals(retrievedAuditRecord.getActivity(), activity);
        Assert.assertNotNull(retrievedAuditRecord.getCreationTime());

        Assert.assertTrue(
                retrievedAuditRecord.getCreationTime().after(timeOfAuditRecording),
                String.format(
                        "retrievedAuditRecord.getCreationTime(): %s, timeOfAuditRecorded: %s",
                        retrievedAuditRecord.getCreationTime(),
                        timeOfAuditRecording
                )
        );

        // when
        final AuditRecordCollection auditRecordCollection = auditApi.getAuditRecords();

        // then
        Assert.assertNotNull(auditRecordCollection);
        Assert.assertNotNull(auditRecordCollection.getAuditRecords(), "auditRecordCollection should contain some audit records");
        Assert.assertTrue(auditRecordCollection.getAuditRecords().length > 0, "auditRecordCollection should contain some audit records");

        //TODO: test with filter
    }


}
