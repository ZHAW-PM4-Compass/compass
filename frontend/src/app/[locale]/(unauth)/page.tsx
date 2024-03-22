import { getTranslations } from 'next-intl/server';



export async function generateMetadata(props: { params: { locale: string } }) {
  const t = await getTranslations({
    locale: props.params.locale,
    namespace: 'Index',
  });

  return {
    title: t('meta_title'),
    description: t('meta_description'),
  };
}

export default function Index() {
  return (
    <div className="text-center mx-auto">
      <p>
        Discover more about the <span className="font-bold">Compass</span> web application for the <span className="font-bold">Stadtmuur organization</span> by visiting our GitHub project.
      </p>
      <p>
        Compass 🧭 is a cutting-edge web application designed to enhance the Stadtmuur organization's operational efficiency. It enables participants to record their working hours, track their mood, monitor exceptional incidents, generate daily reports, and visualize this data comprehensively.
      </p>
    </div>
  );
}
