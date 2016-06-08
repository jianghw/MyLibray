# MyLibray
圆的小世界
### 根目录build.gradle加
allprojects { 
    repositories { 
			... 
			maven { url "https://jitpack.io" } 
		} 
	} 
***********
### app build.gradle加
	dependencies {
	        compile 'com.github.jianghw:MyLibray:v1.0.1'
	}
