/*
 * Sonar C++ Plugin (Community)
 * Copyright (C) 2010-2020 SonarOpenCommunity
 * http://github.com/SonarOpenCommunity/sonar-cxx
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.cxx;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

public class CxxSquidConfigurationTest {

  private static final String VC_KEY = "Visual C++";
  private static final String VC_CHARSET = "UTF8";

  @Test
  public void emptyValueShouldReturnNoDirsOrDefines() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setCompilationPropertiesWithBuildLog(new ArrayList<>(), VC_KEY, VC_CHARSET);
    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isZero();
    softly.assertThat(squidConfig.getDefines().size()).isZero();
    softly.assertAll();
  }

  @Test
  public void emptyValueShouldReturnWhenNull() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setCompilationPropertiesWithBuildLog(null, VC_KEY, VC_CHARSET);
    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isZero();
    assertThat(squidConfig.getDefines().size()).isZero();
    softly.assertAll();
  }

  @Test
  public void emptyValueShouldUseIncludeDirsIfSet() {
    var squidConfig = new CxxSquidConfiguration();
    String[] data = {"dir1", "dir2"};
    squidConfig.setIncludeDirectories(data);
    squidConfig.setCompilationPropertiesWithBuildLog(new ArrayList<>(), VC_KEY, VC_CHARSET);
    assertThat(squidConfig.getIncludeDirectories().size()).isEqualTo(2);
  }

  @Test
  public void correctlyCreatesConfiguration1() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setBaseDir(".");
    var files = new ArrayList<File>();
    files.add(new File("src/test/resources/logfile/vc++13.txt"));
    squidConfig.setCompilationPropertiesWithBuildLog(files, VC_KEY, VC_CHARSET);

    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isEqualTo(13);
    softly.assertThat(squidConfig.getDefines().size()).isEqualTo(26 + 5);
    softly.assertAll();
  }

  @Test
  public void shouldHandleSpecificCommonOptionsCorrectly() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setBaseDir(".");
    var files = new ArrayList<File>();
    files.add(new File("src/test/resources/logfile/platformCommon.txt"));
    squidConfig.setCompilationPropertiesWithBuildLog(files, VC_KEY, VC_CHARSET);

    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isZero();
    List<String> defines = squidConfig.getDefines();
    softly.assertThat(defines.size()).isEqualTo(20 + 5);
    ValidateDefaultAsserts(softly, defines);
    softly.assertThat(defines).contains("_OPENMP 200203");
    softly.assertThat(defines).contains("_WIN32");
    softly.assertThat(defines).contains("_M_IX86 600");
    softly.assertThat(defines).contains("_M_IX86_FP 2");
    softly.assertThat(defines).contains("_WCHAR_T_DEFINED 1");
    softly.assertThat(defines).contains("_NATIVE_WCHAR_T_DEFINED 1");
    softly.assertThat(defines).contains("_VC_NODEFAULTLIB");
    softly.assertThat(defines).contains("_MT");
    softly.assertThat(defines).contains("_DLL");
    softly.assertThat(defines).contains("_DEBUG");
    softly.assertThat(defines).contains("_VC_NODEFAULTLIB");
    softly.assertAll();
  }

  public void shouldHandleSpecificCommonWin32OptionsCorrectly() {
    var squidConfig = new CxxSquidConfiguration();
    var files = new ArrayList<File>();
    files.add(new File("src/test/resources/logfile/platformCommonWin32.txt"));
    squidConfig.setCompilationPropertiesWithBuildLog(files, VC_KEY, VC_CHARSET);

    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isZero();
    List<String> defines = squidConfig.getDefines();
    softly.assertThat(defines.size()).isEqualTo(3);
    ValidateDefaultAsserts(softly, defines);
    softly.assertThat(defines).contains("_WIN32");
    softly.assertAll();
  }

  @Test
  public void shouldHandleSpecificCommonx64OptionsCorrectly() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setBaseDir(".");
    var files = new ArrayList<File>();
    files.add(new File("src/test/resources/logfile/platformCommonX64.txt"));
    squidConfig.setCompilationPropertiesWithBuildLog(files, VC_KEY, VC_CHARSET);

    assertThat(squidConfig.getIncludeDirectories().size()).isZero();
    List<String> defines = squidConfig.getDefines();

    var softly = new SoftAssertions();
    softly.assertThat(defines.size()).isEqualTo(15 + 5);
    ValidateDefaultAsserts(softly, defines);
    softly.assertThat(defines).contains("_Wp64");
    softly.assertThat(defines).contains("_WIN32");
    softly.assertThat(defines).contains("_WIN64");
    softly.assertThat(defines).contains("_M_X64 100");
    softly.assertThat(defines.contains("_M_IX86")).isFalse();
    softly.assertThat(defines).contains("_M_IX86_FP 2");
    softly.assertAll();
  }

  @Test
  public void shouldHandleSpecificV100OptionsCorrectly() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setBaseDir(".");
    var files = new ArrayList<File>();
    files.add(new File("src/test/resources/logfile/platformToolsetv100.txt"));
    squidConfig.setCompilationPropertiesWithBuildLog(files, VC_KEY, VC_CHARSET);

    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isZero();
    List<String> defines = squidConfig.getDefines();
    softly.assertThat(defines.size()).isEqualTo(12 + 6);
    ValidateDefaultAsserts(softly, defines);
    softly.assertThat(defines).contains("_CPPUNWIND");
    softly.assertThat(defines).contains("_M_IX86 600");
    softly.assertThat(defines).contains("_WIN32");
    softly.assertThat(defines).contains("_M_IX86_FP 2");
    softly.assertAll();
  }

  @Test
  public void shouldHandleSpecificV110OptionsCorrectly() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setBaseDir(".");
    var files = new ArrayList<File>();
    files.add(new File("src/test/resources/logfile/platformToolsetv110.txt"));
    squidConfig.setCompilationPropertiesWithBuildLog(files, VC_KEY, VC_CHARSET);

    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isZero();
    List<String> defines = squidConfig.getDefines();
    softly.assertThat(defines.size()).isEqualTo(13 + 5);
    ValidateDefaultAsserts(softly, defines);
    softly.assertThat(defines).contains("__cplusplus_winrt 201009");
    softly.assertThat(defines).contains("_CPPUNWIND");
    softly.assertThat(defines).contains("_M_IX86 600");
    softly.assertThat(defines).contains("_WIN32");
    softly.assertThat(defines).contains("_M_IX86_FP 2");
    softly.assertThat(defines).contains("_MSC_VER 1700");
    softly.assertThat(defines).contains("_MSC_FULL_VER 1700610301");
    softly.assertThat(defines).contains("_ATL_VER 0x0B00");
    softly.assertAll();
  }

  @Test
  public void shouldHandleSpecificV120OptionsCorrectly() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setBaseDir(".");
    var files = new ArrayList<File>();
    files.add(new File("src/test/resources/logfile/platformToolsetv120.txt"));
    squidConfig.setCompilationPropertiesWithBuildLog(files, VC_KEY, VC_CHARSET);

    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isZero();
    List<String> defines = squidConfig.getDefines();
    softly.assertThat(defines.size()).isEqualTo(15 + 6);
    ValidateDefaultAsserts(softly, defines);
    softly.assertThat(defines).contains("__AVX2__ 1");
    softly.assertThat(defines).contains("__AVX__ 1");
    softly.assertThat(defines).contains("__cplusplus_winrt 201009");
    softly.assertThat(defines).contains("_CPPUNWIND");
    softly.assertThat(defines).contains("_M_ARM_FP");
    softly.assertThat(defines).contains("_WIN32");
    softly.assertThat(defines).contains("_M_IX86 600");
    softly.assertThat(defines).contains("_M_IX86_FP 2");
    softly.assertThat(defines).contains("_MSC_VER 1800");
    softly.assertThat(defines).contains("_MSC_FULL_VER 180031101");
    softly.assertThat(defines).contains("_ATL_VER 0x0C00");
    softly.assertAll();
  }

  @Test
  public void shouldHandleSpecificV140OptionsCorrectly() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setBaseDir(".");
    var files = new ArrayList<File>();
    files.add(new File("src/test/resources/logfile/platformToolsetv140.txt"));
    squidConfig.setCompilationPropertiesWithBuildLog(files, VC_KEY, VC_CHARSET);

    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isZero();
    List<String> defines = squidConfig.getDefines();
    assertThat(defines.size()).isEqualTo(15 + 6);
    ValidateDefaultAsserts(softly, defines);
    softly.assertThat(defines).contains("__AVX2__ 1");
    softly.assertThat(defines).contains("__AVX__ 1");
    softly.assertThat(defines).contains("__cplusplus_winrt 201009");
    softly.assertThat(defines).contains("_CPPUNWIND");
    softly.assertThat(defines).contains("_M_ARM_FP");
    softly.assertThat(defines).contains("_M_IX86 600");
    softly.assertThat(defines).contains("_M_IX86_FP 2");
    softly.assertThat(defines).contains("_MSC_VER 1900");
    softly.assertThat(defines).contains("_MSC_FULL_VER 190024215");
    softly.assertThat(defines).contains("_ATL_VER 0x0E00");
    softly.assertAll();
  }

  @Test
  public void shouldHandleTFSAgentV141OptionsCorrectly() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setBaseDir(".");
    var files = new ArrayList<File>();
    files.add(new File("src/test/resources/logfile/TFS-agent-msvc14.txt"));
    squidConfig.setCompilationPropertiesWithBuildLog(files, VC_KEY, VC_CHARSET);

    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isEqualTo(2);
    List<String> defines = squidConfig.getDefines();
    assertThat(defines.size()).isEqualTo(34);
    ValidateDefaultAsserts(softly, defines);
    softly.assertThat(defines).contains("_CPPUNWIND");
    softly.assertThat(defines).contains("_M_IX86 600");
    softly.assertThat(defines).contains("_M_IX86_FP 2");
    softly.assertThat(defines).contains("_MSC_VER 1910");
    softly.assertThat(defines).contains("_MSC_FULL_VER 191024629");
    softly.assertThat(defines).contains("_ATL_VER 0x0E00");
    softly.assertAll();
  }

  @Test
  public void shouldHandleTFSAgentV141mpOptionsCorrectly() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setBaseDir(".");
    var files = new ArrayList<File>();
    files.add(new File("src/test/resources/logfile/TFS-agent-msvc14-mp.txt"));
    squidConfig.setCompilationPropertiesWithBuildLog(files, VC_KEY, VC_CHARSET);

    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isEqualTo(2);
    List<String> defines = squidConfig.getDefines();
    assertThat(defines.size()).isEqualTo(34);
    ValidateDefaultAsserts(softly, defines);
    softly.assertThat(defines).contains("_CPPUNWIND");
    softly.assertThat(defines).contains("_M_IX86 600");
    softly.assertThat(defines).contains("_M_IX86_FP 2");
    softly.assertThat(defines).contains("_MSC_VER 1910");
    softly.assertThat(defines).contains("_MSC_FULL_VER 191024629");
    softly.assertThat(defines).contains("_ATL_VER 0x0E00");
    softly.assertAll();
  }

  @Test
  public void shouldHandleSpecificV141x86OptionsCorrectly() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setBaseDir(".");
    var files = new ArrayList<File>();
    files.add(new File("src/test/resources/logfile/platformToolsetv141x86.txt"));
    squidConfig.setCompilationPropertiesWithBuildLog(files, VC_KEY, VC_CHARSET);

    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isZero();
    List<String> defines = squidConfig.getDefines();
    assertThat(defines.size()).isEqualTo(15 + 12);
    ValidateDefaultAsserts(softly, defines);
    softly.assertThat(defines).contains("_M_IX86 600");
    softly.assertThat(defines).contains("__cplusplus 199711L");
    softly.assertThat(defines).contains("_MSC_VER 1910");
    softly.assertThat(defines).contains("_MSC_FULL_VER 191024629");
    // check atldef.h for _ATL_VER
    softly.assertThat(defines).contains("_ATL_VER 0x0E00");
    softly.assertAll();
  }

  @Test
  public void shouldHandleSpecificV141x64OptionsCorrectly() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setBaseDir(".");
    var files = new ArrayList<File>();
    files.add(new File("src/test/resources/logfile/platformToolsetv141x64.txt"));
    squidConfig.setCompilationPropertiesWithBuildLog(files, VC_KEY, VC_CHARSET);

    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isZero();
    List<String> defines = squidConfig.getDefines();
    assertThat(defines.size()).isEqualTo(15 + 14);
    ValidateDefaultAsserts(softly, defines);
    softly.assertThat(defines.contains("_M_IX86 600")).isFalse();
    softly.assertThat(defines).contains("__cplusplus 199711L");
    softly.assertThat(defines).contains("_MSC_VER 1910");
    softly.assertThat(defines).contains("_MSC_FULL_VER 191024629");
    // check atldef.h for _ATL_VER
    softly.assertThat(defines).contains("_ATL_VER 0x0E00");
    softly.assertAll();
  }

  @Test
  public void shouldHandleBuildLog() {
    var squidConfig = new CxxSquidConfiguration();
    squidConfig.setBaseDir(".");
    var files = new ArrayList<File>();
    files.add(new File("src/test/resources/logfile/ParallelBuildLog.txt"));
    squidConfig.setCompilationPropertiesWithBuildLog(files, VC_KEY, VC_CHARSET);

    var softly = new SoftAssertions();
    softly.assertThat(squidConfig.getIncludeDirectories().size()).isEqualTo(15);
    softly.assertThat(squidConfig.getDefines().size()).isEqualTo(30);
    softly.assertAll();
  }

  @Test
  public void shouldGetSourceFilesList() {
    var squidConfig = new CxxSquidConfiguration();

    var files = new String[]{"testfile", "anotherfile", "thirdfile"};
    for (var filename : files) {
      squidConfig.addCompilationUnitSettings(filename, new CxxCompilationUnitSettings());
    }

    Set<String> sourceFiles = squidConfig.getCompilationUnitSourceFiles();

    assertThat(sourceFiles.size()).isEqualTo(files.length);
    assertThat(files).containsOnly(files);
  }

  private void ValidateDefaultAsserts(SoftAssertions softly, List<String> defines) {
    softly.assertThat(defines).contains("_INTEGRAL_MAX_BITS 64");
    softly.assertThat(defines).contains("_MSC_BUILD 1");
    softly.assertThat(defines).contains("__COUNTER__ 0");
    softly.assertThat(defines).contains("__DATE__ \"??? ?? ????\"");
    softly.assertThat(defines).contains("__FILE__ \"file\"");
    softly.assertThat(defines).contains("__LINE__ 1");
    softly.assertThat(defines).contains("__TIME__ \"??:??:??\"");
    softly.assertThat(defines).contains("__TIMESTAMP__ \"??? ?? ???? ??:??:??\"");
    softly.assertAll();
  }

}
