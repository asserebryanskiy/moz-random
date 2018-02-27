; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{B571AF34-9C4E-4BA5-A244-B3333C06F56A}
AppName=Random Numbers Generator
AppVersion=1.0
;AppVerName=moz-random 1.0
AppPublisher=SC
AppPublisherURL=http://www.example.com/
AppSupportURL=http://www.example.com/
AppUpdatesURL=http://www.example.com/
DefaultDirName={pf}\moz-random
DisableProgramGroupPage=yes
OutputBaseFilename=mozRandomSetup{AppVersion}
SetupIconFile=C:\Users\User\IdeaProjects\moz-random\src\main\deploy\package\windows\moz-random.ico
Compression=lzma
SolidCompression=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "russian"; MessagesFile: "compiler:Languages\Russian.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "moz-random\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{commonprograms}\moz-random"; Filename: "{app}\moz-random.exe"
Name: "{commondesktop}\moz-random"; Filename: "{app}\moz-random.exe"; Tasks: desktopicon

[Run]
Filename: "{app}\moz-random.exe"; Description: "{cm:LaunchProgram,moz-random}"; Flags: nowait postinstall skipifsilent

