/*
 * Corona-Warn-App
 *
 * SAP SE and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package app.coronawarn.server.services.distribution.assembly.diagnosiskeys.structure.directory;

import app.coronawarn.server.common.persistence.domain.DiagnosisKey;
import app.coronawarn.server.services.distribution.assembly.component.CryptoProvider;
import app.coronawarn.server.services.distribution.assembly.diagnosiskeys.structure.directory.decorator.DateAggregatingDecorator;
import app.coronawarn.server.services.distribution.assembly.structure.WritableOnDisk;
import app.coronawarn.server.services.distribution.assembly.structure.directory.Directory;
import app.coronawarn.server.services.distribution.assembly.structure.directory.IndexDirectory;
import app.coronawarn.server.services.distribution.assembly.structure.directory.IndexDirectoryOnDisk;
import app.coronawarn.server.services.distribution.assembly.structure.directory.decorator.indexing.AbstractIndexingDecorator;
import app.coronawarn.server.services.distribution.assembly.structure.directory.decorator.indexing.IndexingDecoratorOnDisk;
import app.coronawarn.server.services.distribution.assembly.structure.util.ImmutableStack;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

public class DiagnosisKeysCountryDirectoryOnDisk extends IndexDirectoryOnDisk<String> {

  private static final String COUNTRY_DIRECTORY = "country";
  private static final String COUNTRY = "DE";

  private final Collection<DiagnosisKey> diagnosisKeys;
  private final CryptoProvider cryptoProvider;

  /**
   * Constructs a {@link DiagnosisKeysCountryDirectoryOnDisk} instance that represents the {@code
   * .../country/:country/...} portion of the diagnosis key directory structure.
   *
   * @param diagnosisKeys  The diagnosis keys processed in the contained sub directories.
   * @param cryptoProvider The {@link CryptoProvider} used for payload signing.
   */
  public DiagnosisKeysCountryDirectoryOnDisk(Collection<DiagnosisKey> diagnosisKeys,
      CryptoProvider cryptoProvider) {
    super(COUNTRY_DIRECTORY, __ -> Set.of(COUNTRY), Object::toString);
    this.diagnosisKeys = diagnosisKeys;
    this.cryptoProvider = cryptoProvider;
  }

  @Override
  public void prepare(ImmutableStack<Object> indices) {
    this.addWritableToAll(__ -> {
      IndexDirectoryOnDisk<LocalDate> dateDirectory = new DiagnosisKeysDateDirectoryOnDisk(diagnosisKeys,
          cryptoProvider);
      return decorateDateDirectory(dateDirectory);
    });
    super.prepare(indices);
  }

  private IndexDirectory<LocalDate, WritableOnDisk> decorateDateDirectory(IndexDirectoryOnDisk<LocalDate> dateDirectory) {
    return new DateAggregatingDecorator(new IndexingDecoratorOnDisk<>(dateDirectory), cryptoProvider);
  }
}
