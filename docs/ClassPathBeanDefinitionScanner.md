`ClassPathBeanDefinitionScanner`

```
 ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider 
 
 Set<BeanDefinitionHolder> doScan(String... basePackages)
 
 public Set<BeanDefinition> findCandidateComponents(String basePackage) 
 
 private Set<BeanDefinition> scanCandidateComponents(String basePackage) 
 
 // 此处完成`AnnotationMetadataReadingVisitor`#annotationSet初始化过程
 MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
 
 
 protected boolean isCandidateComponent(MetadataReader metadataReader) 
 
 AnnotationTypeFilter (父类为AbstractTypeHierarchyTraversingFilter)
 
```





`AnnotationTypeFilter`

```
@Override
public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)

	@Override
	protected boolean matchSelf(MetadataReader metadataReader) {
		// AnnotationMetadata，通常实现有两种AnnotationMetadataReadingVisitor和StandardAnnotationMetadata
		// 此处是AnnotationMetadataReadingVisitor
		AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
		return metadata.hasAnnotation(this.annotationType.getName()) ||
				(this.considerMetaAnnotations && metadata.hasMetaAnnotation(this.annotationType.getName()));
	}
```



`AnnotationMetadataReadingVisitor`

```
@Override
public boolean hasAnnotation(String annotationName) {
   return this.annotationSet.contains(annotationName);
}
```





`AnnotationMetadataReadingVisitor`#annotationSet初始化过程：

 MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);

```
 @Override
public MetadataReader getMetadataReader(Resource resource) throws IOException 


	@Override
	public MetadataReader getMetadataReader(Resource resource) throws IOException {
		return new SimpleMetadataReader(resource, this.resourceLoader.getClassLoader());
	}
	
	
	
	SimpleMetadataReader(Resource resource, @Nullable ClassLoader classLoader) throws IOException {
		InputStream is = new BufferedInputStream(resource.getInputStream());
		ClassReader classReader;
		try {
			classReader = new ClassReader(is);
		}
		catch (IllegalArgumentException ex) {
			throw new NestedIOException("ASM ClassReader failed to parse class file - " +
					"probably due to a new Java class file version that isn't supported yet: " + resource, ex);
		}
		finally {
			is.close();
		}
		// componentScan扫描识别@Componet等注解的关键实现
		AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor(classLoader);
		
		// 初始化visitor信息
		classReader.accept(visitor, ClassReader.SKIP_DEBUG);

		// 赋值
		this.annotationMetadata = visitor;
		// (since AnnotationMetadataReadingVisitor extends ClassMetadataReadingVisitor)
		this.classMetadata = visitor;
		this.resource = resource;
	}
```

