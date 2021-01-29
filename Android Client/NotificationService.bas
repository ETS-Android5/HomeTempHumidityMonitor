B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Service
Version=8
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: true
	
#End Region

Sub Process_Globals
	Private listener As NotificationListener
	Private timer As Timer
	Private response As String
	Private authToken As String
	Private userRegion As String = "u006"
	Private emailAddress As String
	Private password As String
	Private TwoClientFAVerificationRequired As String
End Sub

Sub Service_Create
	listener.Initialize("listener")
End Sub

Sub Service_Start (StartingIntent As Intent)
	If listener.HandleIntent(StartingIntent) Then Return
End Sub

Sub Listener_NotificationPosted (SBN As StatusBarNotification)
	Try
		' Log("NotificationPosted, package = " & SBN.PackageName & ", id = " & SBN.Id & ", text = " & SBN.TickerText)
		Dim p As Phone
		If p.SdkVersion >= 19 Then
			Dim jno As JavaObject = SBN.Notification
			Dim extras As JavaObject = jno.GetField("extras")
			extras.RunMethod("size", Null)
			If SBN.PackageName = "cloyd.smart.home.monitor" Then
				If SBN.Id = 726 Then
					SmartHomeMonitor.IsAirQualityNotificationOnGoing = True
				else If SBN.Id = 725 Then
					SmartHomeMonitor.IsTempHumidityNotificationOnGoing = True
				else If SBN.Id = 727 Then
					SmartHomeMonitor.IsAirQualityNotificationOnGoingBasement = True
				else If SBN.Id = 728 Then
					SmartHomeMonitor.IsTempHumidityNotificationOnGoingbasement = True
				else if SBN.Id = 730 Then
					SmartHomeMonitor.IsOldTempHumidityNotificationOnGoingBasement = True
				else if SBN.Id = 729 Then
					SmartHomeMonitor.IsOldTempHumidityNotificationOnGoing = True
				else if SBN.Id = 731 Then
					SmartHomeMonitor.IsOldAirQualityNotificationOnGoing = True
				else if SBN.Id = 732 Then
					SmartHomeMonitor.IsOldAirQualityNotificationOnGoingBasement = True
				End If
			Else If SBN.PackageName = "com.immediasemi.android.blink" Then
				If File.Exists(File.DirInternal, "account.txt") Then
					Dim List1 As List
					List1.Initialize
					List1 = File.ReadList(File.DirInternal, "account.txt")
					For i = 0 To List1.Size - 1
						If i = 0 Then
							emailAddress = List1.Get(i)
						Else if i = 1 Then
							password = List1.Get(i)
						End If
					Next
				Else If File.Exists(File.DirRootExternal,"account.txt") Then
					File.Copy(File.DirRootExternal,"account.txt",File.DirInternal, "account.txt")
					If File.Exists(File.DirInternal, "account.txt") Then
						Dim List1 As List
						List1.Initialize
						List1 = File.ReadList(File.DirInternal, "account.txt")
						For i = 0 To List1.Size - 1
							If i = 0 Then
								emailAddress = List1.Get(i)
							Else if i = 1 Then
								password = List1.Get(i)
							End If
						Next
					Else
						Return
					End If
				Else
					timer.Enabled = False
					Return
				End If
				
				Dim rs As ResumableSub = RequestAuthToken
				wait for (rs) complete (Result As Object)
				
				If response.Contains("ERROR") Then Return
				
				timer.Initialize("SDTime", 1500)  '1000 Milliseconds = 1 second
				timer.Enabled = True
			End If
		End If
	Catch
		Log(LastException)
	End Try

End Sub

Sub Listener_NotificationRemoved (SBN As StatusBarNotification)
	Try
		'Log("NotificationRemoved, package = " & SBN.PackageName & ", id = " & SBN.Id & ", text = " & SBN.TickerText)
		If SBN.PackageName = "cloyd.smart.home.monitor" Then
			If SBN.Id = 726 Then
				SmartHomeMonitor.IsAirQualityNotificationOnGoing = False
			else If SBN.Id = 725 Then
				SmartHomeMonitor.lngTicks = DateTime.now
				SmartHomeMonitor.lngTicksTempHumid = DateTime.now
				SmartHomeMonitor.IsTempHumidityNotificationOnGoing = False
			else If SBN.Id = 727 Then
				SmartHomeMonitor.IsAirQualityNotificationOnGoingBasement = False
			else If SBN.Id = 728 Then
				SmartHomeMonitor.lngTicksTempHumidBasement = DateTime.now
				SmartHomeMonitor.IsTempHumidityNotificationOnGoingBasement = False
			else If SBN.Id = 730 Then
				SmartHomeMonitor.IsOldTempHumidityNotificationOnGoingBasement = False
			else If SBN.Id = 729 Then
				SmartHomeMonitor.IsOldTempHumidityNotificationOnGoing = False
			else If SBN.Id = 731 Then
				SmartHomeMonitor.IsOldAirQualityNotificationOnGoing = False
			else If SBN.Id = 732 Then
				SmartHomeMonitor.IsOldAirQualityNotificationOnGoingBasement = False
			End If
		Else If SBN.PackageName = "com.immediasemi.android.blink" Then
			timer.Enabled = False
			Dim Intent1 As Intent
			Intent1.Initialize("blink.video.remove", "")
			Dim Phone As Phone
			Phone.SendBroadcastIntent(Intent1)
		End If
	Catch
		Log(LastException)
	End Try

End Sub

Sub Service_Destroy

End Sub

Sub SDTime_tick
	Try
		Dim rs As ResumableSub = GetUnwatchedVideosService
		wait for (rs) complete (Result As Object)
	Catch
		Log(LastException)
	End Try
End Sub

Sub GetUnwatchedVideosService() As ResumableSub
	Try
		Dim rs As ResumableSub = RESTGet("https://rest-" & userRegion &".immedia-semi.com/api/v1/accounts/88438/media/changed?since=-999999999-01-01T00:00:00+18:00&page=1")
		wait for (rs) complete (Result As Object)
		
		If response.Contains("ERROR") Or response.Contains("System is busy, please wait") Then Return Null
		
		Dim unwatchedVideoCount As Int = 0
		Dim parser As JSONParser
		parser.Initialize(response)
		Dim root As Map = parser.NextObject
		Dim media As List = root.Get("media")
		
		Dim n As Long = DateTime.Now
		For Each colmedia As Map In media
			Dim watched As String = colmedia.Get("watched")
			If watched = False Then
				unwatchedVideoCount = unwatchedVideoCount + 1
			End If
		Next
		Log("From Service: Loading unwatched videos took: " & (DateTime.Now - n) & "ms")
		StateManager.SetSetting("UnwatchedVideoClips",unwatchedVideoCount)
		StateManager.SaveSettings
		
		' Create Tasker notifications if videos are ready
		
		If unwatchedVideoCount > 0 Then
			Dim Intent1 As Intent
			Intent1.Initialize("blink.video.add", "")
			Dim Phone As Phone
			Phone.SendBroadcastIntent(Intent1)
			timer.Enabled = False
		Else

		End If
	Catch
		Log(LastException)
	End Try
	Return Null
End Sub

Sub RESTGet(url As String) As ResumableSub
	Try
		Dim j As HttpJob
		response = ""
		j.Initialize("", Me) 'name is empty as it is no longer needed
		j.Download(url)
		j.GetRequest.SetHeader("TOKEN_AUTH", authToken)
		Wait For (j) JobDone(j As HttpJob)
		If j.Success Then
			response = j.GetString
		Else
			response = "ERROR: " & j.ErrorMessage
		End If
		If response.Contains("System is busy, please wait") Then

		End If
		j.Release
	Catch
		response = "ERROR: " & LastException
		Log("RESTDownload LastException: " & LastException)
	End Try
	Log("URL: " & url)
	Log("Response: " & response)
	Return(response)
End Sub

Sub RequestAuthToken As ResumableSub
	' https://github.com/MattTW/BlinkMonitorProtocol
	' http://www.basic4ppc.com:51042/json/index.html
	' https://www.b4x.com/android/forum/threads/b4x-okhttputils2-with-wait-for.79345/#content
	' https://www.b4x.com/android/forum/threads/call-api-rest.89470/#content
	' https://www.b4x.com/android/forum/threads/server-online-json-tree-example.39048/#content
	Try
		Dim jobLogin As HttpJob
		jobLogin.Initialize("", Me)
		jobLogin.PostString("https://rest-prod.immedia-semi.com/api/v4/account/login","email=" &  emailAddress & "&password=" & password)
		jobLogin.GetRequest.SetContentType("application/x-www-form-urlencoded")
		jobLogin.GetRequest.SetHeader("User-Agent",RandomString(12)) '"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0")
		Wait For (jobLogin) JobDone(jobLogin As HttpJob)
		If jobLogin.Success Then
			GetAuthInfo(jobLogin.GetString)
			
'			If TwoClientFAVerificationRequired Then
'				response = "ERROR TwoClientFAVerificationRequired"
'				Return Null
'			End If

			If response.StartsWith("ERROR: ") Or response.Contains("System is busy, please wait") Then
				jobLogin.Release
				Return Null
			Else
				
			End If
		Else
			Log("RequestAuthToken error: " & jobLogin.ErrorMessage)
			jobLogin.Release
			Return Null
		End If
		jobLogin.Release
	Catch
		Log("RequestAuthToken LastException: " & LastException)
	End Try
	Return Null
End Sub

Sub RandomString(length As Int) As String
	Dim abc As String = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
	Dim randomstr As String = ""
	For i = 0 To length - 1
		randomstr = randomstr & (abc.CharAt(Rnd(0,abc.Length)))
	Next
	Return randomstr
End Sub

Sub GetAuthInfo(json As String)
	Try
		Dim parser As JSONParser
		parser.Initialize(json)
		Dim root As Map = parser.NextObject
		Dim force_password_reset As String = root.Get("force_password_reset") 'ignore
		Dim lockout_time_remaining As Int = root.Get("lockout_time_remaining") 'ignore
		Dim authtokenmap As Map = root.Get("authtoken")
		authToken = authtokenmap.Get("authtoken")
		Dim message As String = authtokenmap.Get("message") 'ignore
		Dim client As Map = root.Get("client")
		TwoClientFAVerificationRequired = client.Get("verification_required")
		Dim allow_pin_resend_seconds As Int = root.Get("allow_pin_resend_seconds") 'ignore
		Dim region As Map = root.Get("region")
		Dim code As String = region.Get("code") 'ignore
		userRegion = region.Get("tier")
		Dim description As String = region.Get("description") 'ignore
		Dim account As Map = root.Get("account")
		Dim verification_required As String = account.Get("verification_required") 'ignore
		Dim id As Int = account.Get("id") 'ignore
	Catch
		response = "ERROR: GetAuthInfo - " & LastException
		Log(LastException)
	End Try

End Sub
