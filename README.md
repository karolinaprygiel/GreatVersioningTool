## Great Versioning Tool (gvt)
My own version control system.

### How to use
Run the class `uj.java.pwj2020.Gvt` with a command as a parameter.

#### Commands
##### init
Initializes new directory

##### add
Adds to gvt file specified as a parameter, takes an optional message.

##### detach
Detach from gvt file specified as a parameter, takes an optional message.

##### checkout
Restores files to the state of the specific version specified in the parameter.

##### commit
Commit changes to the file specified as a parameter, takes an optional message.

##### history
Displays the version history in format: `{version number}: {commit message}`
If no parameters specified, all versions are displayed.
Parameter `-last {n}` displays last n versions.

##### version
Shows details about version specified as a paramter.



