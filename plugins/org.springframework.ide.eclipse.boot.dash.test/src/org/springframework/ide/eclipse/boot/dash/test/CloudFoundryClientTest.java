/*******************************************************************************
 * Copyright (c) 2015, 2016 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.boot.dash.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.ide.eclipse.boot.dash.test.CloudFoundryTestHarness.APP_DEPLOY_TIMEOUT;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.cloudfoundry.client.lib.ApplicationLogListener;
import org.cloudfoundry.client.lib.StreamingLogToken;
import org.cloudfoundry.client.v2.serviceinstances.ServiceInstanceResource.ServiceInstanceResourceBuilder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.CFAppState;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.CFApplication;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.CFApplicationDetail;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.CFBuildpack;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.CFClientParams;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.CFCloudDomain;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.CFService;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.CFSpace;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.CFStack;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.v2.CFPushArguments;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.v2.DefaultClientRequestsV2;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.v2.DefaultCloudFoundryClientFactoryV2;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.v2.ReactorUtils;
import org.springframework.ide.eclipse.boot.dash.model.UserInteractions;
import org.springframework.ide.eclipse.boot.dash.util.CancelationTokens;
import org.springframework.ide.eclipse.boot.test.BootProjectTestHarness;
import org.springframework.ide.eclipse.boot.util.RetryUtil;
import org.springframework.ide.eclipse.boot.util.StringUtil;
import org.springframework.ide.eclipse.boot.util.Thunk;
import org.springsource.ide.eclipse.commons.cloudfoundry.client.diego.SshClientSupport;
import org.springsource.ide.eclipse.commons.cloudfoundry.client.diego.SshHost;
import org.springsource.ide.eclipse.commons.frameworks.test.util.ACondition;
import org.springsource.ide.eclipse.commons.livexp.util.ExceptionUtil;
import org.springsource.ide.eclipse.commons.tests.util.StsTestUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CloudFoundryClientTest {

	private static final String CFAPPS_IO = "cfapps.io";

	private DefaultClientRequestsV2 client = createClient(CfTestTargetParams.fromEnv());

	@Rule
	public TestBracketter bracketer = new TestBracketter();

	@Rule
	public CloudFoundryServicesHarness services = new CloudFoundryServicesHarness(client);

	@Rule
	public CloudFoundryApplicationHarness appHarness = new CloudFoundryApplicationHarness(client);


	@After
	public void teardown() throws Exception {
		StsTestUtil.cleanUpProjects();
	}

	public BootProjectTestHarness projects = new BootProjectTestHarness(ResourcesPlugin.getWorkspace());

	private UserInteractions ui = Mockito.mock(UserInteractions.class);

	private static DefaultClientRequestsV2 createClient(CFClientParams fromEnv) {
		try {
			DefaultCloudFoundryClientFactoryV2 factory = new DefaultCloudFoundryClientFactoryV2();
			return (DefaultClientRequestsV2) factory.getClient(fromEnv);
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	@Test
	public void testGetSpaces() throws Exception {
		int success = 0;
		int failed  = 0;

		Exception error = null;
		for (int i = 0; i < 5; i++) {
			try {
				long start = System.currentTimeMillis();
				List<CFSpace> spaces = client.getSpaces();
				long duration = System.currentTimeMillis() - start;
				System.out.println("getSpaces -> "+spaces.size()+" spaces in "+ duration + " ms");
				success++;
			} catch (Exception e) {
				error = e;
				failed++;
				System.out.println("getSpaces -> "+ExceptionUtil.getMessage(e));
			}
		}
		System.out.println("getSpaces failure rate = "+failed + "/" +(success+failed));
		if (failed>0) {
			throw new IOException("getSpaces failure rate = "+failed + "/" +(success+failed), error);
		}
	}

	@Test
	public void testGetApplicationDetails() throws Exception {
		String appName = appHarness.randomAppName();

		try (CFPushArguments params = new CFPushArguments()) {
			params.setAppName(appName);
			params.setApplicationData(getTestZip("testapp"));
			params.setBuildpack("staticfile_buildpack");
			params.setNoStart(true);
			push(params);
		}

		{
			CFApplicationDetail appDetails = client.getApplication(appName);
			assertEquals(0, appDetails.getRunningInstances());
			assertEquals(CFAppState.STOPPED, appDetails.getState());
			assertEquals(ImmutableList.of(), appDetails.getInstanceDetails());
		}

		client.restartApplication(appName, CancelationTokens.NULL);
		{
			CFApplicationDetail appDetails = client.getApplication(appName);
			assertEquals(1, appDetails.getRunningInstances());
			assertEquals(CFAppState.STARTED, appDetails.getState());
			assertEquals(1, appDetails.getInstanceDetails().size());
		}
	}

	@Test
	public void testPushAndBindServices() throws Exception {
		//This test fails occasionally because service binding is 'unreliable'. Had a long discussion
		// with Ben Hale. The gist is errors happen and should be expected in distributed world.
		//They are coming from 'AppDirect' which manages the services. The errors are mediated through cloudfoundry
		// which doesn't knwow how it should handle them. So it passed the buck onto the its callers.
		//In this case.... cf-java-client which does the same thing and passes them to us.
		//All the reasons why they can't handle these errors also apply to us, which means that
		//the operation is simply unreliable and so failure is an expected outcome even when everything
		//works correctly.
		//To avoid this test case from failing too often we retry it a few times.
		RetryUtil.retryTimes("testPushAndBindServices", 4, () -> {
			String appName = appHarness.randomAppName();

			String service1 = services.createTestService();
			String service2 = services.createTestService();
			String service3 = services.createTestService(); //An extra unused service (makes this a better test).

			CFPushArguments params = new CFPushArguments();
			params.setAppName(appName);
			params.setApplicationData(getTestZip("testapp"));
			params.setBuildpack("staticfile_buildpack");
			params.setServices(ImmutableList.of(service1, service2));
			push(params);

			assertEquals(ImmutableSet.of(service1, service2), getBoundServiceNames(appName));

			client.bindAndUnbindServices(appName, ImmutableList.of(service1)).get();
			assertEquals(ImmutableSet.of(service1), getBoundServiceNames(appName));

			client.bindAndUnbindServices(appName, ImmutableList.of(service2)).get();
			assertEquals(ImmutableSet.of(service2), getBoundServiceNames(appName));

			client.bindAndUnbindServices(appName, ImmutableList.of()).get();
			assertEquals(ImmutableSet.of(), getBoundServiceNames(appName));
		});
	}

	private Set<String> getBoundServiceNames(String appName) throws Exception {
		return client.getBoundServicesSet(appName).get();
	}

	@Test
	public void testPushAndBindHostAndDomain() throws Exception {
		String appName = appHarness.randomAppName();

		for (int i = 0; i < 2; i++) {
			//Why this loop? Because there was bug which CF V2 that made second push fail to bind to
			// map a host that was previously mapped.
			if (i>0) {
				System.out.println("Delete app");
				client.deleteApplication(appName);
			}

			System.out.println("Pushing "+(i+1));
			CFPushArguments params = new CFPushArguments();
			params.setAppName(appName);
			params.setApplicationData(getTestZip("testapp"));
			params.setBuildpack("staticfile_buildpack");
			params.setRoutes(ImmutableList.of(appName+"."+CFAPPS_IO));

			push(params);
		}

		System.out.println("Pushing SUCCESS");

		CFApplicationDetail app = client.getApplication(appName);
		assertNotNull("Expected application to exist after push: " + appName, app);

		assertEquals(ImmutableSet.of(appName+"."+CFAPPS_IO), ImmutableSet.copyOf(app.getUris()));
	}

	@Test
	public void testPushAndBindMultipleHosts() throws Exception {
		String[] hostNames = {
				appHarness.randomAppName(),
				appHarness.randomAppName()
		};
		String appName = hostNames[0];

		CFPushArguments params = new CFPushArguments();
		params.setAppName(hostNames[0]);
		params.setApplicationData(getTestZip("testapp"));
		params.setBuildpack("staticfile_buildpack");

		Set<String> routes = ImmutableSet.copyOf(Stream.of(hostNames)
				.map((host) -> host + "." + CFAPPS_IO)
				.collect(Collectors.toList())
		);
		params.setRoutes(routes);

		push(params);

		System.out.println("Pushing SUCCESS");

		CFApplicationDetail app = client.getApplication(appName);
		assertNotNull("Expected application to exist after push: " + appName, app);

		assertEquals(routes, ImmutableSet.copyOf(app.getUris()));
	}

	@Test
	public void testPushAndSetRoutes() throws Exception {
		String[] hostNames = {
				appHarness.randomAppName(),
				appHarness.randomAppName()
		};
		String appName = hostNames[0];

		CFPushArguments params = new CFPushArguments();
		params.setAppName(hostNames[0]);
		params.setApplicationData(getTestZip("testapp"));
		params.setBuildpack("staticfile_buildpack");

		Set<String> routes = ImmutableSet.copyOf(Stream.of(hostNames)
				.map((host) -> host + "." + CFAPPS_IO)
				.collect(Collectors.toList())
		);
		params.setRoutes(routes);

		push(params);

		System.out.println("Pushing SUCCESS");

		{
			CFApplicationDetail app = client.getApplication(appName);
			assertNotNull("Expected application to exist after push: " + appName, app);
			assertEquals(routes, ImmutableSet.copyOf(app.getUris()));
		}

		doSetRoutesTest(appName, ImmutableSet.of());

		for (String route : routes) {
			doSetRoutesTest(appName, ImmutableSet.of(route));
		}

	}

	private void doSetRoutesTest(String appName, ImmutableSet<String> routes) throws Exception {
		ReactorUtils.get(client.setRoutes(appName, routes));
		CFApplicationDetail app = client.getApplication(appName);
		assertEquals(routes, ImmutableSet.copyOf(app.getUris()));
	}

	@Test
	public void testPushAndSetEnv() throws Exception {
		String appName = appHarness.randomAppName();

		CFPushArguments params = new CFPushArguments();
		params.setAppName(appName);
		params.setRoutes(appName+"."+CFAPPS_IO);
		params.setApplicationData(getTestZip("testapp"));
		params.setBuildpack("staticfile_buildpack");
		params.setEnv(ImmutableMap.of(
				"foo", "foo_value",
				"bar", "bar_value"
		));
		push(params);

		CFApplicationDetail app = client.getApplication(appName);
		assertNotNull("Expected application to exist after push: " + appName, app);
		ACondition.waitFor("app content to be availabe", 10_000, () -> {
			String content = IOUtils.toString(new URI("http://" + appName + '.' + CFAPPS_IO + "/test.txt"));
			assertTrue(content.length() > 0);
			assertTrue(content.contains("content"));
		});

		{
			Map<String, String> env = client.getEnv(appName).get();
			assertEquals("foo_value", env.get("foo"));
			assertEquals("bar_value", env.get("bar"));
			assertEquals(2, env.size());
		}

		client.setEnvVars(appName, ImmutableMap.of("other", "value")).get();
		{
			Map<String, String> env = client.getEnv(appName).get();
			assertEquals("value", env.get("other"));
			assertEquals(1, env.size());
		}

		//This last piece is commented because it fails.
		//See: https://www.pivotaltracker.com/story/show/116804259

		// The last var doesn't get removed. Not sure how to fix it.
		// But eventually we won't even be using 'setEnvVars' it will be part of the push.
		// and its not going to be our problem to fix that.
//		client.updateApplicationEnvironment(appName, ImmutableMap.of()).get();
//		{
//			Map<String, Object> env = client.getEnv(appName).get();
//			assertEquals(0, env.size());
//		}
	}

	@Test
	public void testDeleteApplication()	 throws Exception {
		String appName = appHarness.randomAppName();

		CFPushArguments params = new CFPushArguments();
		params.setAppName(appName);
		params.setApplicationData(getTestZip("testapp"));
		params.setBuildpack("staticfile_buildpack");
		push(params);

		CFApplicationDetail app = client.getApplication(appName);
		assertTrue(client.applicationExists(appName));
		assertNotNull("Expected application to exist after push: " + appName, app);

		client.deleteApplication(appName);
		app = client.getApplication(appName);
		assertNull("Expected application to be deleted after delete: " + appName, app);
		assertFalse(client.applicationExists(appName));
	}

	@Test
	public void testStopApplication()	 throws Exception {
		String appName = appHarness.randomAppName();

		CFPushArguments params = new CFPushArguments();
		params.setAppName(appName);
		params.setApplicationData(getTestZip("testapp"));
		params.setBuildpack("staticfile_buildpack");
		push(params);

		final CFApplicationDetail runningApp = client.getApplication(appName);
		assertNotNull("Expected application to exist after push: " + appName, runningApp);

		new ACondition("wait for app '"+ appName +"'to be RUNNING", APP_DEPLOY_TIMEOUT) {
			public boolean test() throws Exception {
				assertAppRunState(1, runningApp.getRunningInstances(), CFAppState.STARTED, runningApp.getState());
				return true;
			}
		};

		client.stopApplication(appName);
		final CFApplicationDetail stoppedApp = client.getApplication(appName);

		new ACondition("wait for app '"+ appName +"'to be STOPPED", APP_DEPLOY_TIMEOUT) {
			public boolean test() throws Exception {
				assertAppRunState(0, stoppedApp.getRunningInstances(), CFAppState.STOPPED, stoppedApp.getState());
				return true;
			}
		};
	}

	@Test
	public void getServices() throws Exception {
		String[] serviceNames = new String[3];
		for (int i = 0; i < serviceNames.length; i++) {
			serviceNames[i] = services.createTestService();
		}

		ImmutableSet<String> actualServiceNames = ImmutableSet.copyOf(
				client.getServices().stream()
				.map(CFService::getName)
				.collect(Collectors.toList())
		);
		for (String s : serviceNames) {
			assertTrue(s+" not found in "+actualServiceNames, actualServiceNames.contains(s));
		}
	}

	@Test
	public void testServiceCreateAndDelete() throws Exception {
		String serviceName = services.randomServiceName();
		client.createService(serviceName, "cloudamqp", "lemur").get();
		List<CFService> services = client.getServices();
		assertServices(services, serviceName);
		client.deleteService(serviceName).get();

		assertNoServices(client.getServices(), serviceName);
	}


	@Test
	public void testGetBoundServices() throws Exception {
		String appName = appHarness.randomAppName();
		String service1 = services.createTestService();
		String service2 = services.createTestService();
		String service3 = services.createTestService();

		CFPushArguments params = new CFPushArguments();
		params.setAppName(appName);
		params.setApplicationData(getTestZip("testapp"));
		params.setBuildpack("staticfile_buildpack");
		params.setServices(ImmutableList.of(service1, service2));
		push(params);

		List<CFApplication> allApps = client.getApplicationsWithBasicInfo();
		CFApplication app = null;
		for (CFApplication a : allApps) {
			if (a.getName().equals(appName)) {
				app = a;
			}
		}
		assertEquals(ImmutableSet.of(service1, service2), ImmutableSet.copyOf(app.getServices()));

		app = client.getApplication(appName);
		assertEquals(ImmutableSet.of(service1, service2), ImmutableSet.copyOf(app.getServices()));
	}


	@Test
	public void testGetDomains() throws Exception {
		client = createClient(CfTestTargetParams.fromEnv());
		List<CFCloudDomain> domains = client.getDomains();
		assertEquals(CFAPPS_IO, domains.get(0).getName());

		Set<String> names = Flux.fromIterable(domains)
			.map(CFCloudDomain::getName)
			.toList()
			.map(ImmutableSet::copyOf)
			.get();
		assertContains(names,
				"projectreactor.org",
				"projectreactor.io",
				"dsyer.com"
		);
	}

	@Test
	public void testGetBuildpacks() throws Exception {
		client = createClient(CfTestTargetParams.fromEnv());
		List<CFBuildpack> buildpacks = client.getBuildpacks();

		Set<String> names = Flux.fromIterable(buildpacks)
				.map(CFBuildpack::getName)
				.toList()
				.map(ImmutableSet::copyOf)
				.get();

		assertContains(names,
			"staticfile_buildpack",
			"java_buildpack",
			"ruby_buildpack"
		);
	}

	@Test
	public void testGetStacks() throws Exception {
		client = createClient(CfTestTargetParams.fromEnv());
		List<CFStack> stacks = client.getStacks();

		Set<String> names = Flux.fromIterable(stacks)
			.map(CFStack::getName)
			.toList()
			.map(ImmutableSet::copyOf)
			.get();

		assertContains(names,
				"cflinuxfs2"
		);
	}

	@Test
	public void testApplicationLogConnection() throws Exception {
		client = createClient(CfTestTargetParams.fromEnv());

		String appName = appHarness.randomAppName();
		ApplicationLogListener listener = mock(ApplicationLogListener.class);
		Mono<StreamingLogToken> token = client.streamLogs(appName, listener);
		assertNotNull(token);

		CFPushArguments params = new CFPushArguments();
		params.setAppName(appName);
		params.setApplicationData(getTestZip("testapp"));
		params.setBuildpack("staticfile_buildpack");
		push(params);

		BootDashModelTest.waitForJobsToComplete();
		verify(listener, atLeastOnce()).onMessage(any());
	}

	@Test
	public void testGetApplicationBuildpack() throws Exception {
		String appName = appHarness.randomAppName();

		CFPushArguments params = new CFPushArguments();
		params.setAppName(appName);
		params.setApplicationData(getTestZip("testapp"));
		params.setBuildpack("staticfile_buildpack");
		push(params);

		//Note we try to get the app two different ways because retrieving the info in
		// each case is slightly different.

		{
			CFApplicationDetail app = client.getApplication(appName);
			assertEquals("staticfile_buildpack", app.getBuildpackUrl());
		}

		{
			List<CFApplication> allApps = client.getApplicationsWithBasicInfo();
			CFApplication app = null;
			for (CFApplication a : allApps) {
				if (a.getName().equals(appName)) {
					app = a;
				}
			}
			assertEquals("staticfile_buildpack", app.getBuildpackUrl());
		}
	}


	@Test
	public void testGetApplicationStack() throws Exception {
		String appName = appHarness.randomAppName();
		String stackName = "cflinuxfs2";

		CFPushArguments params = new CFPushArguments();
		params.setAppName(appName);
		params.setApplicationData(getTestZip("testapp"));
		params.setBuildpack("staticfile_buildpack");
		params.setStack(stackName);
		push(params);

		//Note we try to get the app two different ways because retrieving the info in
		// each case is slightly different.

		{
			CFApplicationDetail app = client.getApplication(appName);
			assertEquals(stackName, app.getStack());
		}

		{
			List<CFApplication> allApps = client.getApplicationsWithBasicInfo();
			CFApplication app = null;
			for (CFApplication a : allApps) {
				if (a.getName().equals(appName)) {
					app = a;
				}
			}
			assertEquals(stackName, app.getStack());
		}
	}

	@Test
	public void testGetApplicationTimeout() throws Exception {
		String appName = appHarness.randomAppName();
		int timeout = 67;

		CFPushArguments params = new CFPushArguments();
		params.setAppName(appName);
		params.setApplicationData(getTestZip("testapp"));
		params.setBuildpack("staticfile_buildpack");
		params.setTimeout(timeout);
		push(params);

		//Note we try to get the app two different ways because retrieving the info in
		// each case is slightly different.

		{
			CFApplicationDetail app = client.getApplication(appName);
			assertEquals(timeout, (int)app.getTimeout());
		}

		{
			List<CFApplication> allApps = client.getApplicationsWithBasicInfo();
			CFApplication app = null;
			for (CFApplication a : allApps) {
				if (a.getName().equals(appName)) {
					app = a;
				}
			}
			assertEquals(timeout, (int)app.getTimeout());
		}
	}

	@Test
	public void testGetApplicationCommand() throws Exception {
		String appName = appHarness.randomAppName();
		String command = "something interesting";

		CFPushArguments params = new CFPushArguments();
		params.setAppName(appName);
		params.setApplicationData(getTestZip("testapp"));
		params.setBuildpack("staticfile_buildpack");
		params.setCommand(command);
		params.setNoStart(true); // Our command is bogus so starting won't work
		push(params);

		//Note we try to get the app two different ways because retrieving the info in
		// each case is slightly different.

		{
			CFApplicationDetail app = client.getApplication(appName);
			assertEquals(command, app.getCommand());
		}

		{
			List<CFApplication> allApps = client.getApplicationsWithBasicInfo();
			CFApplication app = null;
			for (CFApplication a : allApps) {
				if (a.getName().equals(appName)) {
					app = a;
				}
			}
			assertEquals(command, app.getCommand());
		}
	}

	@Test public void testSshSupport() throws Exception {
		String appName = appHarness.randomAppName();

		CFPushArguments params = new CFPushArguments();
		params.setAppName(appName);
		params.setApplicationData(getTestZip("testapp"));
		params.setBuildpack("staticfile_buildpack");
		push(params);

		SshClientSupport sshSupport = client.getSshClientSupport();
		SshHost sshHost = sshSupport.getSshHost();
		System.out.println(sshHost);
		assertEquals("ssh.run.pivotal.io", sshHost.getHost());
		assertEquals(2222, sshHost.getPort());
		assertTrue(StringUtil.hasText(sshHost.getFingerPrint()));

		assertTrue(StringUtil.hasText(sshSupport.getSshCode()));
		UUID appGuid = client.getApplication(appName).getGuid();
		String sshUser = sshSupport.getSshUser(appGuid, 0);
		System.out.println("sshUser = "+sshUser);
		assertTrue(StringUtil.hasText(sshUser));

		String code = sshSupport.getSshCode();
		System.out.println("sshCode = "+code);
		assertTrue(StringUtil.hasText(code));
	}

	@Test public void testGetServiceDashboardUrl() throws Exception {
		String serviceName = services.createTestService();
		CFService service = null;
		for (CFService s : client.getServices()) {
			if (s.getName().equals(serviceName)) {
				service = s;
			}
		}
		String dashUrl = service.getDashboardUrl();
		assertNotNull(dashUrl);
		assertTrue(dashUrl.startsWith("http"));
	}

	@Test public void pushAndStartCanBeCanceled() throws Exception {
		String appName = appHarness.randomAppName();
		IProject project = projects.createBootWebProject("slow-starter");
		File jarFile = BootJarPackagingTest.packageAsJar(project, ui);

		CancelationTokens cancelationTokens = new CancelationTokens();
		try (CFPushArguments params = new CFPushArguments()) {
			params.setAppName(appName);
			params.setRoutes(appName+"."+CFAPPS_IO);
			params.setApplicationData(jarFile);

			long starting = System.currentTimeMillis();
			Future<Void> pushResult = doAsync(() -> {
				System.out.println("Pushing...");
				client.push(params, cancelationTokens.create());
				long duration = System.currentTimeMillis() - starting;
				System.out.println("Pushing took: "+duration+ " ms");
			});
			Thread.sleep(Duration.ofSeconds(10).toMillis());
			long cancelTime = System.currentTimeMillis();
			System.out.println("Canceling...");
			cancelationTokens.cancelAll();

			try {
				pushResult.get(5, TimeUnit.SECONDS); // Cancel should happen pretty 'fast'!
				fail("push completed but it should have been canceled");
			} catch (ExecutionException e) { // real exception is wrapped in EE by Future.get
				long duration = System.currentTimeMillis() - cancelTime;
				assertEquals(OperationCanceledException.class, e.getCause().getClass());
				System.out.println("\nPush Canceled after: "+duration +" ms");
			}
		}

		try (CFPushArguments params = new CFPushArguments()) {
			params.setAppName(appName);
			params.setRoutes(appName+"."+CFAPPS_IO);
			params.setApplicationData(jarFile);
			params.setNoStart(true);
			client.push(params, CancelationTokens.NULL);
		}


		long starting = System.currentTimeMillis();
		System.out.println("Starting...");
		Future<Void> startResult = doAsync(() -> {
			client.restartApplication(appName, cancelationTokens.create());
			long duration = System.currentTimeMillis() - starting;
			System.out.println("started in "+duration+" ms");
		});

		Thread.sleep(5000);
		long cancelTime = System.currentTimeMillis();
		cancelationTokens.cancelAll();
		try {
			startResult.get(5, TimeUnit.SECONDS);
		} catch (ExecutionException e) {
			long duration = System.currentTimeMillis() - cancelTime;
			assertEquals(OperationCanceledException.class, e.getCause().getClass());
			System.out.println("\nRestart Canceled after "+duration+" ms");
		}

	}


	/////////////////////////////////////////////////////////////////////////////

	private Future<Void> doAsync(Thunk task) {
		CompletableFuture<Void> result = new CompletableFuture<Void>();
		Job job = new Job("Async task") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					task.call();
					result.complete(null);
				} catch (Throwable e) {
					result.completeExceptionally(e);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		return result;
	}

	private void push(CFPushArguments _params) throws Exception {
		try (CFPushArguments params = _params) {
			client.push(params, CancelationTokens.NULL);
		}
	}

	private void assertContains(Set<String> strings, String... expecteds) {
		for (String e : expecteds) {
			assertContains(e, strings);
		}
	}

	private void assertContains(String expected, Set<String> names) {
		assertTrue("Expected '"+expected+"' not found in: "+names, names.contains(expected));
	}

	private void assertNoServices(List<CFService> services, String serviceName) throws Exception {
		Set<String> names = services.stream().map(CFService::getName).collect(Collectors.toSet());
		assertFalse(names.contains(serviceName));
	}

	private void assertServices(List<CFService> services, String... serviceNames) throws Exception {
		Set<String> names = services.stream().map(CFService::getName).collect(Collectors.toSet());
		assertContains(names, serviceNames);
	}

	private void assertAppRunState(int expectedInstances, int actualInstances, CFAppState expectedRequestedState, CFAppState actualRequestedState) {
		assertEquals("Expected running instances does not match actual running instances: ", expectedInstances, actualInstances);
		assertEquals("Expected requested app state does not match actual requested app state: ", expectedRequestedState, actualRequestedState);
	}

	private File getTestZip(String fileName) {
		File sourceWorkspace = new File(
				StsTestUtil.getSourceWorkspacePath("org.springframework.ide.eclipse.boot.dash.test"));
		File file = new File(sourceWorkspace, fileName + ".zip");
		Assert.isTrue(file.exists(), ""+ file);
		return file;
	}

}