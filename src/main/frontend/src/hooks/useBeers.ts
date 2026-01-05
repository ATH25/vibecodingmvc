import useAsync from './useAsync';
import useQueryParams from './useQueryParams';
import { emitErrorToast } from '../lib/errors';
import { listBeers } from '../services/beerService';

export type UseBeersParams = {
  page?: string | number;
  size?: string | number;
  beerName?: string;
  beerStyle?: string;
  sort?: string;
};

export function useBeers(initialParams: UseBeersParams = {}) {
  const { params, setParams } = useQueryParams<Record<string, string>>();

  const page = Number(params.page ?? initialParams.page ?? 1);
  const size = Number(params.size ?? initialParams.size ?? 10);
  const beerName = params.beerName ?? initialParams.beerName ?? '';
  const beerStyle = params.beerStyle ?? initialParams.beerStyle ?? '';
  const sort = params.sort ?? initialParams.sort ?? '';

  const {
    data,
    loading,
    error,
    run: fetchBeers,
    setData,
  } = useAsync(
    async () =>
      listBeers({
        page,
        size,
        beerName: beerName || undefined,
        beerStyle: beerStyle || undefined,
        sort: sort || undefined,
      }),
    {
      auto: true,
      deps: [page, size, beerName, beerStyle, sort],
      onError: (err) => emitErrorToast({ source: 'useBeers', error: err }),
    },
  );

  const handlePageChange = (newPage: number) => {
    setParams({ ...params, page: newPage });
  };

  const handleFilterChange = (filters: Partial<Pick<UseBeersParams, 'beerName' | 'beerStyle'>>) => {
    setParams({ ...params, ...filters, page: 1 }); // Reset to page 1 on filter change
  };

  const handleSortChange = (sortBy: string, dir: 'asc' | 'desc') => {
    setParams({ ...params, sort: `${sortBy},${dir}` });
  };

  return {
    beers: data?.content ?? [],
    pagination: {
      page,
      size,
      totalElements: data?.totalElements ?? 0,
      totalPages: data?.totalPages ?? 0,
    },
    loading,
    error,
    filters: {
      beerName,
      beerStyle,
    },
    sort,
    fetchBeers,
    setData,
    handlePageChange,
    handleFilterChange,
    handleSortChange,
  };
}

export default useBeers;
