yuicompress-anttask
===================

anttask for yuicompress

Requirements
------------
Apache Ant must be installed on your system.

Build
-----
    ant

Usage
-----
step 1: define classpath for ant tasks in your build file
  
    <path id="tasks.classpath">
      <fileset dir="${antlib.dir}">
      <include name="**/*.jar" />
      </fileset>
    </path>
  
step 2: define the ant task 'yuicompress' in your build file

    <taskdef name="yuicompress" classname="yuicompress.anttask.YuiCompressorTask" classpathref="tasks.classpath" loaderref="tasks.classpath.loader" />
  
step 3: defined a target to compress js and/or css

    <target name="confusion">
      <yuicompress todir="${build.root.dir}" inputcharset="utf-8" outputcharset="utf-8" munge="true" preserveallsemicolons="true" preservestringliterals="true">
        <fileset dir="${build.root.dir}">
          <include name="**/*.js" />
          <include name="**/*.css" />
        </fileset>
      </yuicompress>
    </target>
